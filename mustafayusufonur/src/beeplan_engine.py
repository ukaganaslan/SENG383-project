"""
BeePlan Scheduler Engine
Core Requirements Implementation
(Revised for Day Ordering and Logic Constraints)
"""
from __future__ import annotations
from dataclasses import dataclass, field
from datetime import time, timedelta, datetime
from enum import Enum
import logging
from typing import Dict, List, Optional, Tuple

# Logger
logger = logging.getLogger(__name__)
if not logger.handlers:
    logging.basicConfig(level=logging.INFO)

TimeInterval = Tuple[time, time]

# Günlerin sayısal ve kronolojik sırası (Önemli Düzeltme)
DAYS_MAP = {
    "Monday": 0,
    "Tuesday": 1,
    "Wednesday": 2,
    "Thursday": 3,
    "Friday": 4,
    "Saturday": 5,
    "Sunday": 6
}

# --- Yardımcılar ---
def time_to_minutes(t: time) -> int:
    return t.hour * 60 + t.minute

def minutes_to_time(minutes: int) -> time:
    h = (minutes // 60) % 24
    m = minutes % 60
    return time(h, m)

def add_minutes(t: time, minutes: int) -> time:
    # Basit wrap-around kontrolü için datetime kullanımı
    dt = datetime(2000, 1, 1, t.hour, t.minute) + timedelta(minutes=minutes)
    return dt.time()

def intervals_overlap(start1: time, end1: time, start2: time, end2: time) -> bool:
    return time_to_minutes(start1) < time_to_minutes(end2) and time_to_minutes(start2) < time_to_minutes(end1)

# --- Modeller ---
class RoomType(str, Enum):
    LAB = "LAB"
    CLASSROOM = "CLASSROOM"

class Department(str, Enum):
    CENG = "CENG"
    SENG = "SENG"
    COMMON = "COMMON"

class SchedulingConflictError(Exception): pass
class ResourceDataError(Exception): pass

@dataclass
class ScheduleSlot:
    day: str
    start_time: time
    duration_minutes: int
    room_id: int
    course_id: int
    instructor_id: int
    
    @property
    def end_time(self):
        return add_minutes(self.start_time, self.duration_minutes)

@dataclass
class Instructor:
    id: int
    name: str
    availability_hours: Dict[str, List[TimeInterval]] = field(default_factory=dict)

@dataclass
class Course:
    id: int
    name: str
    duration: int
    required_room_type: RoomType
    size: int
    department: Department = Department.COMMON
    year_level: int = 1
    is_elective: bool = False
    assigned_instructor_id: Optional[int] = None
    related_theory_id: Optional[int] = None

    @property
    def is_theory(self):
        return self.required_room_type == RoomType.CLASSROOM

@dataclass
class Room:
    id: int
    capacity: int
    type: RoomType

# --- Algoritma Motoru ---
class SchedulerEngine:
    def __init__(self, courses: Dict[int, Course], rooms: Dict[int, Room], instructors: Dict[int, Instructor]):
        self.courses = courses
        self.rooms = rooms
        self.instructors = instructors
        # Constraint: No courses Friday 13:20-15:10
        self.FRIDAY_BLOCK_START = time(13, 20)
        self.FRIDAY_BLOCK_END = time(15, 10)

    def _validate_resources(self):
        if not self.courses: raise ResourceDataError("No courses loaded.")
        for c in self.courses.values():
            if c.required_room_type == RoomType.LAB and c.size > 40:
                logger.warning(f"Course {c.id} violates Lab Capacity constraint (>40).")

    def is_valid(self, slot: ScheduleSlot, schedule: List[ScheduleSlot]) -> bool:
        course = self.courses[slot.course_id]
        room = self.rooms[slot.room_id]
        instructor = self.instructors[slot.instructor_id]
        slot_end = slot.end_time

        # 1. Fiziksel Kontroller
        if room.capacity < course.size: return False
        if room.type != course.required_room_type: return False

        # 2. Cuma Yasağı (Exam Block)
        if slot.day == "Friday":
            if intervals_overlap(slot.start_time, slot_end, self.FRIDAY_BLOCK_START, self.FRIDAY_BLOCK_END):
                return False

        # 3. Lab-Teori İlişkisi (Düzeltildi)
        if course.required_room_type == RoomType.LAB and course.related_theory_id:
            for s in schedule:
                if s.course_id == course.related_theory_id:
                    theory_day_idx = DAYS_MAP.get(s.day, 7)
                    lab_day_idx = DAYS_MAP.get(slot.day, 7)

                    # Kural: Lab, Teoriden daha önceki bir günde OLAMAZ.
                    if lab_day_idx < theory_day_idx:
                        return False 
                    
                    # Kural: Aynı gün ise, Lab saati Teoriden sonra başlamalı.
                    if lab_day_idx == theory_day_idx:
                        if time_to_minutes(s.end_time) > time_to_minutes(slot.start_time):
                            return False
            # Not: Backtracking algoritmasında 'sorting' sayesinde Teori genelde önce yerleşir.
            # Ancak teori henüz yerleşmediyse burada hata vermiyoruz, çünkü ileride yerleşebilir.
            # Algoritma yapısı gereği Teoriler önce işleniyor.

        # 4. Eğitmen Günlük Yükü (Max 4 Saat Teori)
        if course.is_theory:
            daily_mins = sum(s.duration_minutes for s in schedule 
                             if s.instructor_id == slot.instructor_id 
                             and s.day == slot.day 
                             and self.courses[s.course_id].is_theory)
            if daily_mins + course.duration > 240: return False

        # 5. Çakışmalar (Oda, Hoca, Öğrenci Grubu)
        for s in schedule:
            if s.day != slot.day: continue
            if not intervals_overlap(slot.start_time, slot_end, s.start_time, s.end_time): continue
            
            if s.room_id == slot.room_id: return False # Oda dolu
            if s.instructor_id == slot.instructor_id: return False # Hoca dolu

            other = self.courses[s.course_id]
            # Aynı sınıf çakışması
            if course.year_level == other.year_level: return False
            # 3. Sınıf vs Seçmeli
            if (course.year_level == 3 and other.is_elective) or (other.year_level == 3 and course.is_elective):
                return False

        # 6. Eğitmen Müsaitliği
        windows = instructor.availability_hours.get(slot.day, [])
        fits = any(time_to_minutes(w[0]) <= time_to_minutes(slot.start_time) and 
                   time_to_minutes(slot_end) <= time_to_minutes(w[1]) for w in windows)
        return fits

    def generate_schedule(self) -> List[ScheduleSlot]:
        self._validate_resources()
        
        # Heuristic: Teorileri Lablardan ÖNCE planla.
        # Bu sıralama, is_valid içindeki Lab kontrolünün çalışabilmesi için kritiktir.
        sorted_courses = sorted(self.courses.values(), key=lambda c: (
            1 if c.required_room_type == RoomType.LAB else 0, # 0=Classroom önce
            -c.year_level,
            -c.size
        ))

        schedule = []
        
        def backtrack(idx):
            if idx == len(sorted_courses): return schedule
            
            course = sorted_courses[idx]
            valid_rooms = [r for r in self.rooms.values() if r.type == course.required_room_type and r.capacity >= course.size]
            instr_ids = [course.assigned_instructor_id] if course.assigned_instructor_id else list(self.instructors.keys())

            for i_id in instr_ids:
                if i_id not in self.instructors: continue
                instr = self.instructors[i_id]
                
                # availability_hours içinde hangi günler varsa onları döngüye al
                # Ancak burada gün sıralamasına uymak zorunda değiliz, müsaitliğe bakıyoruz
                for day, windows in instr.availability_hours.items():
                    candidates = self._generate_starts(windows, course.duration)
                    for start in candidates:
                        slot = ScheduleSlot(day, start, course.duration, 0, course.id, i_id)
                        for room in valid_rooms:
                            slot.room_id = room.id
                            if self.is_valid(slot, schedule):
                                schedule.append(slot)
                                if backtrack(idx + 1): return schedule
                                schedule.pop()
            return None

        result = backtrack(0)
        if not result: raise SchedulingConflictError("Çözüm bulunamadı! Kısıtlamalar çok sıkı olabilir.")
        return result

    def _generate_starts(self, windows, duration, step=30):
        res = []
        for s, e in windows:
            curr = time_to_minutes(s)
            end = time_to_minutes(e) - duration
            while curr <= end:
                res.append(minutes_to_time(curr))
                curr += step
        return res
"""
BeePlan Data Loader
Handles JSON Import
"""
import json
from datetime import time
from beeplan_engine import Instructor, Room, Course, RoomType, Department

class DataLoader:
    @staticmethod
    def _parse_time(t_str):
        h, m = map(int, t_str.split(':'))
        return time(h, m)

    @staticmethod
    def load_json(filepath: str):
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)

        instructors = {}
        for d in data['instructors']:
            avail = {day: [(DataLoader._parse_time(s), DataLoader._parse_time(e)) for s, e in wins] 
                     for day, wins in d['availability'].items()}
            inst = Instructor(d['id'], d['name'], avail)
            instructors[inst.id] = inst

        rooms = {}
        for d in data['rooms']:
            rooms[d['id']] = Room(d['id'], d['capacity'], RoomType(d['type']))

        courses = {}
        for d in data['courses']:
            c = Course(
                id=d['id'], name=d['name'], duration=d['duration'],
                required_room_type=RoomType(d['room_type']),
                size=d['size'],
                department=Department(d.get('department', 'COMMON')),
                year_level=d.get('year', 1),
                is_elective=d.get('is_elective', False),
                assigned_instructor_id=d.get('assigned_instructor_id'),
                related_theory_id=d.get('related_theory_id')
            )
            courses[c.id] = c
            
        return courses, rooms, instructors
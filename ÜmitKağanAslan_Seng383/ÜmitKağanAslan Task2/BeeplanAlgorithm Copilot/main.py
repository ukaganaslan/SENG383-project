"""
BeePlan Main Application
"""
import logging
from beeplan_engine import SchedulerEngine, SchedulingConflictError
from data_loader import DataLoader

def main():
    print("--- BeePlan Course Scheduler ---")
    
    # 1. Veri Yükleme
    try:
        courses, rooms, instructors = DataLoader.load_json("beeplan_data.json")
        print(f"Loaded: {len(courses)} courses, {len(rooms)} rooms, {len(instructors)} instructors.")
    except Exception as e:
        print(f"Veri yükleme hatası: {e}")
        return

    # 2. Motoru Başlatma
    engine = SchedulerEngine(courses, rooms, instructors)

    # 3. Çizelge Oluşturma
    try:
        print("Çizelge hesaplanıyor (Lütfen bekleyin)...")
        schedule = engine.generate_schedule()
        
        print("\n--- BAŞARILI ÇİZELGE ---")
        # Sonucu gün ve saate göre sıralayalım
        schedule.sort(key=lambda x: (x.day, x.start_time))
        
        for s in schedule:
            c_name = courses[s.course_id].name
            i_name = instructors[s.instructor_id].name
            print(f"[{s.day} {s.start_time.strftime('%H:%M')}-{s.end_time.strftime('%H:%M')}] "
                  f"{c_name} | Room: {s.room_id} | Instr: {i_name}")

    except SchedulingConflictError as e:
        print(f"\nÇizelgeleme Başarısız: {e}")
    except Exception as e:
        print(f"\nBeklenmeyen Hata: {e}")

if __name__ == "__main__":
    main()
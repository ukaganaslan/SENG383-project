import sys
import os
from datetime import time
from PyQt5.QtWidgets import (QApplication, QMainWindow, QWidget, QVBoxLayout,
                             QHBoxLayout, QPushButton, QTableWidget, QTableWidgetItem,
                             QFileDialog, QMessageBox, QLabel, QHeaderView, QAbstractItemView,
                             QTabWidget, QLineEdit, QSpinBox, QComboBox, QFormLayout,
                             QGroupBox, QListWidget, QTimeEdit, QCheckBox)
from PyQt5.QtGui import QColor, QFont
from PyQt5.QtCore import Qt, QTime

# Önceki dosyalarımızı import ediyoruz
from data_loader import DataLoader, Course, Room, Instructor, RoomType
from beeplan_engine import SchedulerEngine, SchedulingConflictError, DAYS_MAP

class BeePlanWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("BeePlan - Course Scheduler")
        self.setGeometry(100, 100, 1200, 800)

        # Veri saklama
        self.courses = {}
        self.rooms = {}
        self.instructors = {}
        self.engine = None
        self.schedule = []

        # ID sayaçları
        self.next_course_id = 0
        self.next_instructor_id = 0

        # Arayüzü Kur
        self.init_ui()

    def init_ui(self):
        # Ana Widget
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        main_layout = QVBoxLayout()
        central_widget.setLayout(main_layout)

        # Bilgi Etiketi
        self.lbl_status = QLabel("Welcome to BeePlan. Add instructors, rooms, and courses to get started.")
        self.lbl_status.setAlignment(Qt.AlignCenter)
        self.lbl_status.setStyleSheet("color: gray; margin: 5px; font-size: 12px;")
        main_layout.addWidget(self.lbl_status)

        # Sekmeli Arayüz
        self.tabs = QTabWidget()
        main_layout.addWidget(self.tabs)

        # Sekme 1: Hocalar
        self.tab_instructors = QWidget()
        self.init_instructors_tab()
        self.tabs.addTab(self.tab_instructors, "👨‍🏫 Instructors")

        # Sekme 2: Sınıflar/Odalar
        self.tab_rooms = QWidget()
        self.init_rooms_tab()
        self.tabs.addTab(self.tab_rooms, "🏫 Rooms")

        # Sekme 3: Dersler
        self.tab_courses = QWidget()
        self.init_courses_tab()
        self.tabs.addTab(self.tab_courses, "📚 Courses")

        # Sekme 4: Program
        self.tab_schedule = QWidget()
        self.init_schedule_tab()
        self.tabs.addTab(self.tab_schedule, "📅 Schedule")

        # Alt Panel (Ana Butonlar)
        bottom_layout = QHBoxLayout()

        self.btn_gen = QPushButton("⚙️ Generate Schedule")
        self.btn_gen.setStyleSheet("background-color: #2ecc71; color: white; font-weight: bold; padding: 12px; font-size: 14px;")
        self.btn_gen.clicked.connect(self.generate_schedule)

        self.btn_report = QPushButton("📄 View Report")
        self.btn_report.setStyleSheet("background-color: #9b59b6; color: white; font-weight: bold; padding: 12px; font-size: 14px;")
        self.btn_report.clicked.connect(self.show_report)
        self.btn_report.setEnabled(False)

        bottom_layout.addWidget(self.btn_gen)
        bottom_layout.addWidget(self.btn_report)

        main_layout.addLayout(bottom_layout)

    def init_instructors_tab(self):
        layout = QVBoxLayout()
        self.tab_instructors.setLayout(layout)

        # Form Grubu
        form_group = QGroupBox("Add New Instructor")
        form_layout = QFormLayout()

        self.instr_name = QLineEdit()
        self.instr_name.setPlaceholderText("e.g., Dr. Smith")

        form_layout.addRow("Name:", self.instr_name)

        btn_add_instr = QPushButton("➕ Add Instructor")
        btn_add_instr.setStyleSheet("background-color: #3498db; color: white; padding: 8px;")
        btn_add_instr.clicked.connect(self.add_instructor)
        form_layout.addRow(btn_add_instr)

        form_group.setLayout(form_layout)
        layout.addWidget(form_group)

        # Liste
        list_group = QGroupBox("Current Instructors")
        list_layout = QVBoxLayout()

        self.instr_list = QListWidget()
        list_layout.addWidget(self.instr_list)

        btn_remove_instr = QPushButton("🗑️ Remove Selected")
        btn_remove_instr.setStyleSheet("background-color: #e74c3c; color: white; padding: 8px;")
        btn_remove_instr.clicked.connect(self.remove_instructor)
        list_layout.addWidget(btn_remove_instr)

        list_group.setLayout(list_layout)
        layout.addWidget(list_group)

    def init_rooms_tab(self):
        layout = QVBoxLayout()
        self.tab_rooms.setLayout(layout)

        # Form Grubu
        form_group = QGroupBox("Add New Room")
        form_layout = QFormLayout()

        self.room_id = QLineEdit()
        self.room_id.setPlaceholderText("e.g., A101")

        self.room_capacity = QSpinBox()
        self.room_capacity.setMinimum(10)
        self.room_capacity.setMaximum(500)
        self.room_capacity.setValue(30)

        self.room_type = QComboBox()
        self.room_type.addItems(["CLASSROOM", "LAB"])

        form_layout.addRow("Room ID:", self.room_id)
        form_layout.addRow("Capacity:", self.room_capacity)
        form_layout.addRow("Type:", self.room_type)

        btn_add_room = QPushButton("➕ Add Room")
        btn_add_room.setStyleSheet("background-color: #3498db; color: white; padding: 8px;")
        btn_add_room.clicked.connect(self.add_room)
        form_layout.addRow(btn_add_room)

        form_group.setLayout(form_layout)
        layout.addWidget(form_group)

        # Liste
        list_group = QGroupBox("Current Rooms")
        list_layout = QVBoxLayout()

        self.room_list = QListWidget()
        list_layout.addWidget(self.room_list)

        btn_remove_room = QPushButton("🗑️ Remove Selected")
        btn_remove_room.setStyleSheet("background-color: #e74c3c; color: white; padding: 8px;")
        btn_remove_room.clicked.connect(self.remove_room)
        list_layout.addWidget(btn_remove_room)

        list_group.setLayout(list_layout)
        layout.addWidget(list_group)

    def init_courses_tab(self):
        layout = QVBoxLayout()
        self.tab_courses.setLayout(layout)

        # Form Grubu
        form_group = QGroupBox("Add New Course")
        form_layout = QFormLayout()

        self.course_name = QLineEdit()
        self.course_name.setPlaceholderText("e.g., Introduction to Programming")

        self.course_duration = QSpinBox()
        self.course_duration.setMinimum(30)
        self.course_duration.setMaximum(240)
        self.course_duration.setValue(90)
        self.course_duration.setSuffix(" min")

        self.course_size = QSpinBox()
        self.course_size.setMinimum(1)
        self.course_size.setMaximum(500)
        self.course_size.setValue(30)
        self.course_size.setToolTip("Number of students")

        self.course_room_type = QComboBox()
        self.course_room_type.addItems(["CLASSROOM", "LAB"])

        self.course_instructor = QComboBox()
        self.course_instructor.setPlaceholderText("Select instructor")

        form_layout.addRow("Course Name:", self.course_name)
        form_layout.addRow("Duration:", self.course_duration)
        form_layout.addRow("Class Size:", self.course_size)
        form_layout.addRow("Room Type:", self.course_room_type)
        form_layout.addRow("Instructor:", self.course_instructor)

        btn_add_course = QPushButton("➕ Add Course")
        btn_add_course.setStyleSheet("background-color: #3498db; color: white; padding: 8px;")
        btn_add_course.clicked.connect(self.add_course)
        form_layout.addRow(btn_add_course)

        form_group.setLayout(form_layout)
        layout.addWidget(form_group)

        # Liste
        list_group = QGroupBox("Current Courses")
        list_layout = QVBoxLayout()

        self.course_list = QListWidget()
        list_layout.addWidget(self.course_list)

        btn_remove_course = QPushButton("🗑️ Remove Selected")
        btn_remove_course.setStyleSheet("background-color: #e74c3c; color: white; padding: 8px;")
        btn_remove_course.clicked.connect(self.remove_course)
        list_layout.addWidget(btn_remove_course)

        list_group.setLayout(list_layout)
        layout.addWidget(list_group)

    def init_schedule_tab(self):
        layout = QVBoxLayout()
        self.tab_schedule.setLayout(layout)

        # Tablo (Timetable)
        self.table = QTableWidget()
        self.setup_table()
        layout.addWidget(self.table)

    def setup_table(self):
        # Sütunlar: Pazartesi - Cuma (Hafta sonu opsiyonel eklenebilir)
        days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
        self.table.setColumnCount(len(days))
        self.table.setHorizontalHeaderLabels(days)
        
        # Satırlar: 09:00 - 17:00 (Yarım saatlik dilimler)
        # 09:00'dan 17:00'ye kadar -> 8 saat * 2 = 16 dilim
        start_hour = 9
        slots = []
        for h in range(start_hour, 18):
            slots.append(f"{h:02d}:00")
            if h != 17: slots.append(f"{h:02d}:30")
        
        self.table.setRowCount(len(slots))
        self.table.setVerticalHeaderLabels(slots)
        
        # Tablo Ayarları
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.Stretch)
        self.table.setEditTriggers(QAbstractItemView.NoEditTriggers) # Düzenlemeyi kapat
        self.table.setSelectionMode(QAbstractItemView.NoSelection) # Seçimi kapat

    def add_instructor(self):
        name = self.instr_name.text().strip()
        if not name:
            QMessageBox.warning(self, "Warning", "Please enter instructor name!")
            return

        instr_id = self.next_instructor_id

        # Varsayılan müsaitlik: Pazartesi-Cuma 09:00-17:00
        default_availability = {
            "Monday": [(time(9, 0), time(17, 0))],
            "Tuesday": [(time(9, 0), time(17, 0))],
            "Wednesday": [(time(9, 0), time(17, 0))],
            "Thursday": [(time(9, 0), time(17, 0))],
            "Friday": [(time(9, 0), time(17, 0))]
        }

        self.instructors[instr_id] = Instructor(
            id=instr_id,
            name=name,
            availability_hours=default_availability
        )
        self.next_instructor_id += 1

        self.instr_list.addItem(f"[ID: {instr_id}] {name} (Mon-Fri 09:00-17:00)")
        self.course_instructor.addItem(f"{name} (ID: {instr_id})", instr_id)
        self.instr_name.clear()

        self.lbl_status.setText(f"Added instructor: {name}")

    def remove_instructor(self):
        current = self.instr_list.currentRow()
        if current < 0:
            QMessageBox.warning(self, "Warning", "Please select an instructor to remove!")
            return

        item_text = self.instr_list.item(current).text()
        instr_id = int(item_text.split('[ID: ')[1].split(']')[0])

        # Derslerde kullanılıyor mu kontrol et
        for course in self.courses.values():
            if course.assigned_instructor_id == instr_id:
                QMessageBox.warning(self, "Warning", f"This instructor is assigned to '{course.name}'. Remove the course first!")
                return

        del self.instructors[instr_id]
        self.instr_list.takeItem(current)

        # Combo'dan da kaldır
        for i in range(self.course_instructor.count()):
            if self.course_instructor.itemData(i) == instr_id:
                self.course_instructor.removeItem(i)
                break

        self.lbl_status.setText(f"Removed instructor ID: {instr_id}")

    def add_room(self):
        room_id = self.room_id.text().strip()
        if not room_id:
            QMessageBox.warning(self, "Warning", "Please enter room ID!")
            return

        if room_id in self.rooms:
            QMessageBox.warning(self, "Warning", "Room ID already exists!")
            return

        capacity = self.room_capacity.value()
        room_type_str = self.room_type.currentText()
        room_type_enum = RoomType[room_type_str]  # CLASSROOM or LAB

        self.rooms[room_id] = Room(id=room_id, capacity=capacity, type=room_type_enum)
        self.room_list.addItem(f"{room_id} - {room_type_str} (Capacity: {capacity})")
        self.room_id.clear()

        self.lbl_status.setText(f"Added room: {room_id}")

    def remove_room(self):
        current = self.room_list.currentRow()
        if current < 0:
            QMessageBox.warning(self, "Warning", "Please select a room to remove!")
            return

        item_text = self.room_list.item(current).text()
        room_id = item_text.split(' - ')[0]

        del self.rooms[room_id]
        self.room_list.takeItem(current)

        self.lbl_status.setText(f"Removed room: {room_id}")

    def add_course(self):
        name = self.course_name.text().strip()
        if not name:
            QMessageBox.warning(self, "Warning", "Please enter course name!")
            return

        if self.course_instructor.currentIndex() < 0:
            QMessageBox.warning(self, "Warning", "Please select an instructor!")
            return

        duration = self.course_duration.value()
        size = self.course_size.value()
        room_type_str = self.course_room_type.currentText()
        room_type_enum = RoomType[room_type_str]  # CLASSROOM or LAB
        instructor_id = self.course_instructor.currentData()

        course_id = self.next_course_id
        self.courses[course_id] = Course(
            id=course_id,
            name=name,
            duration=duration,
            required_room_type=room_type_enum,
            size=size,
            assigned_instructor_id=instructor_id
        )
        self.next_course_id += 1

        self.course_list.addItem(f"[ID: {course_id}] {name} - {duration}min ({size} students) - {self.instructors[instructor_id].name}")
        self.course_name.clear()

        self.lbl_status.setText(f"Added course: {name}")

    def remove_course(self):
        current = self.course_list.currentRow()
        if current < 0:
            QMessageBox.warning(self, "Warning", "Please select a course to remove!")
            return

        item_text = self.course_list.item(current).text()
        course_id = int(item_text.split('[ID: ')[1].split(']')[0])

        del self.courses[course_id]
        self.course_list.takeItem(current)

        self.lbl_status.setText(f"Removed course ID: {course_id}")

    def generate_schedule(self):
        # Veri kontrolü
        if not self.instructors:
            QMessageBox.warning(self, "Warning", "Please add at least one instructor!")
            return
        if not self.rooms:
            QMessageBox.warning(self, "Warning", "Please add at least one room!")
            return
        if not self.courses:
            QMessageBox.warning(self, "Warning", "Please add at least one course!")
            return

        self.lbl_status.setText("Generating schedule... Please wait.")
        QApplication.processEvents() # Arayüzün donmasını engelle

        try:
            # Engine'i güncelle
            self.engine = SchedulerEngine(self.courses, self.rooms, self.instructors)
            self.schedule = self.engine.generate_schedule()
            self.update_timetable_view()
            self.lbl_status.setText("Schedule generated successfully!")
            self.btn_report.setEnabled(True)
            self.tabs.setCurrentIndex(3) # Schedule sekmesine geç
            QMessageBox.information(self, "Success", "Schedule generated without conflicts!")
        except SchedulingConflictError as e:
            self.lbl_status.setText("Failed to generate schedule.")
            QMessageBox.warning(self, "Conflict Error", str(e))
        except Exception as e:
            self.lbl_status.setText("Unexpected Error.")
            QMessageBox.critical(self, "Error", str(e))

    def update_timetable_view(self):
        # Önce tüm span'ları temizle
        for row in range(self.table.rowCount()):
            for col in range(self.table.columnCount()):
                self.table.setSpan(row, col, 1, 1)

        self.table.clearContents()

        # Renk Paleti (Dersleri ayırmak için)
        colors = [QColor(255, 235, 59), QColor(130, 224, 170), QColor(133, 193, 233),
                  QColor(245, 176, 65), QColor(215, 189, 226), QColor(241, 148, 138)]

        for slot in self.schedule:
            # Sütun İndeksi (Gün)
            col_idx = DAYS_MAP.get(slot.day)
            if col_idx is None or col_idx > 4: continue # Sadece Pzt-Cuma gösteriyoruz

            # Satır İndeksi (Saat)
            # Başlangıç saati 09:00 kabul ettik. (9 * 60 = 540 dakika)
            start_min = slot.start_time.hour * 60 + slot.start_time.minute
            base_min = 9 * 60 # 09:00

            row_idx = (start_min - base_min) // 30 # 30 dakikalık dilimler

            if row_idx < 0 or row_idx >= self.table.rowCount(): continue

            # Süreye göre kaç hücre kaplayacak?
            span_rows = slot.duration_minutes // 30

            # Hücre İçeriği
            course = self.courses[slot.course_id]
            instr = self.instructors[slot.instructor_id]
            room = self.rooms[slot.room_id]

            text = f"{course.name}\n({room.type.value} - {room.id})\n{instr.name}"

            # Hücreyi Oluştur
            item = QTableWidgetItem(text)
            item.setBackground(colors[slot.course_id % len(colors)])
            item.setTextAlignment(Qt.AlignCenter)
            item.setFont(QFont("Arial", 9, QFont.Bold))

            # Önce span'ı ayarla
            if span_rows > 1:
                self.table.setSpan(row_idx, col_idx, span_rows, 1)

            # Sonra item'ı koy
            self.table.setItem(row_idx, col_idx, item)

    def show_report(self):
        msg = "--- SCHEDULE REPORT ---\n\n"
        # Basit bir metin raporu
        sorted_schedule = sorted(self.schedule, key=lambda x: (DAYS_MAP.get(x.day, 7), x.start_time))
        
        for s in sorted_schedule:
             c_name = self.courses[s.course_id].name
             i_name = self.instructors[s.instructor_id].name
             msg += f"[{s.day} {s.start_time.strftime('%H:%M')}] {c_name} | {i_name}\n"
             
        QMessageBox.information(self, "Report", msg)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = BeePlanWindow()
    window.show()
    sys.exit(app.exec_())
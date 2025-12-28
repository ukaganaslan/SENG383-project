# 🐝 BeePlan - Automated Course Scheduler

An intelligent course scheduling system that automatically generates conflict-free timetables using backtracking algorithms with constraint satisfaction.

[![Python Version](https://img.shields.io/badge/python-3.8+-blue.svg)](https://www.python.org/downloads/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![PyQt5](https://img.shields.io/badge/GUI-PyQt5-orange.svg)](https://pypi.org/project/PyQt5/)

## 📋 Table of Contents

- [Features](#features)
- [Demo](#demo)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Algorithm](#algorithm)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **🎯 Automated Scheduling**: Generates optimal timetables using backtracking algorithm
- **🏫 Multi-Resource Management**: Handle instructors, rooms, and courses
- **⚡ Constraint Satisfaction**: Enforces multiple scheduling rules:
  - Room capacity validation
  - Instructor availability checking
  - Time conflict prevention
  - Friday exam block (13:20-15:10) restriction
  - Maximum 4-hour daily teaching load for instructors
- **🖥️ User-Friendly GUI**: Intuitive PyQt5 interface with tabbed navigation
- **📊 Visual Timetable**: Interactive weekly schedule grid view
- **📝 Detailed Reports**: Generate comprehensive schedule reports

## 🎬 Demo

![BeePlan Main Interface](docs/screenshots/main_interface.png)

> **Note**: Screenshots are available in the `/docs/screenshots` folder.

## 🚀 Installation

### Prerequisites

- Python 3.8 or higher
- pip (Python package installer)

### Step 1: Clone the Repository

```bash
git clone https://github.com/ukaganaslan/SENG383-project.git
cd SENG383-project
```

### Step 2: Install Dependencies

```bash
pip install -r requirements.txt
```

**Required packages:**
```
PyQt5>=5.15.0
python-docx>=1.2.0
```

## 💻 Usage

### Running the Application

```bash
python gui.py
```

### Basic Workflow

1. **Add Instructors**
   - Navigate to "Instructors" tab
   - Enter instructor name
   - Click "Add Instructor"
   - Default availability: Monday-Friday 09:00-17:00

2. **Add Rooms**
   - Navigate to "Rooms" tab
   - Enter room ID (e.g., "A101")
   - Set capacity and type (CLASSROOM/LAB)
   - Click "Add Room"

3. **Add Courses**
   - Navigate to "Courses" tab
   - Enter course details:
     - Course name
     - Duration (minutes)
     - Class size
     - Required room type
     - Assigned instructor
   - Click "Add Course"

4. **Generate Schedule**
   - Click "Generate Schedule" button
   - View the timetable in "Schedule" tab
   - Click "View Report" for detailed information

### Sample Data

To test with sample data, use the included `beeplan_data.json`:

```bash
# Load via GUI: File > Load JSON > beeplan_data.json
```

## 📁 Project Structure

```
SENG383-project/
├── src/
│   ├── gui.py                  # Main GUI application
│   ├── beeplan_engine.py       # Scheduling algorithm
│   ├── data_loader.py          # JSON data loader
│   └── main.py                 # CLI version
├── docs/
│   ├── screenshots/            # Application screenshots
│   ├── diagrams/               # Class & Activity diagrams
│   └── BeePlan_Final_Report.pdf
├── video/
│   └── presentation.mp4        # Demo video
├── requirements.txt            # Python dependencies
├── README.md                   # This file
└── beeplan_data.json          # Sample data

```

## 🧠 Algorithm

BeePlan uses a **Backtracking Algorithm** with **Constraint Satisfaction Problem (CSP)** approach:

### Key Components

1. **Constraint Checking (`is_valid`)**
   - Room capacity ≥ class size
   - Room type matches course requirement
   - No time conflicts (room/instructor/student group)
   - Instructor availability window check
   - Friday 13:20-15:10 exam block avoidance
   - Maximum 4-hour daily theory teaching limit

2. **Backtracking Search**
   - Sorts courses by priority (theory courses first)
   - Tries all valid combinations of:
     - Days (Monday-Friday)
     - Time slots (30-minute intervals)
     - Rooms (matching type and capacity)
     - Instructors (with availability)
   - Backtracks on conflicts
   - Returns first valid complete schedule

### Time Complexity

- **Worst Case**: O(b^n) where:
  - b = branching factor (possible slot assignments)
  - n = number of courses
- **Optimizations**:
  - Early constraint checking
  - Intelligent course ordering
  - Instructor availability pre-filtering

## 🛠️ Technologies

- **Language**: Python 3.13
- **GUI Framework**: PyQt5
- **Development Tools**:
  - GitHub Copilot (GUI design)
  - DeepSeek (error handling)
  - Claude (code optimization)
- **Version Control**: Git/GitHub

## 📊 Features Breakdown

| Feature | Status |
|---------|--------|
| Automated Scheduling | ✅ |
| GUI Interface | ✅ |
| Constraint Validation | ✅ |
| Visual Timetable | ✅ |
| Report Generation | ✅ |
| JSON Import/Export | ✅ |
| Custom Availability | ⚠️ (Default only) |
| Multi-semester Support | ❌ (Future) |

## 🐛 Known Issues

1. **Visual Display**: Cell span in schedule table may not perfectly represent course duration (core logic is correct)
2. **Time Range**: Fixed to 09:00-17:00 (not configurable)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Mustafa Yusuf Onur** - Student B
- GitHub: [@ukaganaslan](https://github.com/ukaganaslan)
- Project Link: [https://github.com/ukaganaslan/SENG383-project](https://github.com/ukaganaslan/SENG383-project)

## 🙏 Acknowledgments

- Course: SENG 383 - Software Engineering
- Semester: Fall 2025
- AI Tools: GitHub Copilot, DeepSeek, Claude
- Inspiration: Real-world university scheduling challenges

## 📞 Support

If you have any questions or issues, please open an issue on GitHub or contact the developer.

---

**Note**: This project was developed as part of SENG 383 course requirements and demonstrates the application of software engineering principles including clean code, error handling, and AI-assisted development.

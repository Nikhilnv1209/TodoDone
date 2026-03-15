# TodoApp - BMAD Specification Document

**Version:** 1.0
**Date:** 2025-03-15
**Status:** Draft for Approval

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Functional Requirements](#2-functional-requirements)
3. [Non-Functional Requirements](#3-non-functional-requirements)
4. [User Stories](#4-user-stories)
5. [Screens & UI Structure](#5-screens--ui-structure)
6. [Architecture Overview](#6-architecture-overview)
7. [Data Models](#7-data-models)
8. [API/Service Interfaces](#8-apiservice-interfaces)
9. [Testing Strategy](#9-testing-strategy)
10. [Implementation Phases](#10-implementation-phases)
11. [Design References](#11-design-references)

---

## 1. Executive Summary

### Project Overview
A comprehensive project management Todo application for Android with full task hierarchy support, calendar integration, AI-powered task extraction, and a beautiful, intuitive UI.

### Core Value Propositions
1. **Unlimited Task Nesting** - Break down complex projects into manageable subtasks
2. **Smart Calendar View** - Visual date-based task planning with synced components
3. **AI Task Assistant** - Extract tasks from text and calendar, get smart suggestions
4. **Offline-First** - Full functionality without internet, sync interfaces for future cloud

### Target Users
- Knowledge workers managing multiple projects
- Students organizing assignments
- Anyone needing hierarchical task management

---

## 2. Functional Requirements

### 2.1 Core Task Management

| ID | Requirement | Priority |
|---|---|---|
| FR-001 | Create task with title, description, due date, priority, tags | Must |
| FR-002 | Edit task details | Must |
| FR-003 | Delete task (soft delete with undo) | Must |
| FR-004 | Mark task complete/incomplete with timestamp | Must |
| FR-005 | Unlimited nesting: tasks can have subtasks recursively | Must |
| FR-006 | Subtasks contribute to parent completion percentage | Should |
| FR-007 | Archive completed tasks after 30 days | Could |

### 2.2 Reminders & Recurring

| ID | Requirement | Priority |
|---|---|---|
| FR-008 | Due date with time selection | Must |
| FR-009 | Custom reminders (relative: 15min, 1hr, 1day; absolute: specific time) | Must |
| FR-010 | Recurring tasks: Daily, Weekdays, Weekly, Monthly, Custom | Must |
| FR-011 | Recurring completion generates next occurrence | Must |
| FR-012 | Skip/complete all future occurrences | Should |

### 2.3 Organization & Views

| ID | Requirement | Priority |
|---|---|---|
| FR-013 | Calendar view: Horizontal date picker + task list (synced) | Must |
| FR-014 | Task list views: All, Today, Tomorrow, Upcoming, Someday | Must |
| FR-015 | Filter by: Priority, Tags, Project | Must |
| FR-016 | Smart lists: Overdue, No Due Date, Recently Completed | Must |
| FR-017 | Search tasks by title/description | Must |
| FR-018 | Sort: Due date, Priority, Created, Manual order | Should |

### 2.4 Data & Sync

| ID | Requirement | Priority |
|---|---|---|
| FR-019 | Local persistence with Room database | Must |
| FR-020 | Offline-first: full functionality without internet | Must |
| FR-021 | Sync interfaces defined for future cloud implementation | Must |
| FR-022 | Data export/import (JSON/CSV backup) | Could |

### 2.5 AI Features

| ID | Requirement | Priority |
|---|---|---|
| FR-023 | Extract tasks from unstructured text using AI | Must |
| FR-024 | Parse calendar events and suggest preparatory tasks | Must |
| FR-025 | Local pattern learning for smart suggestions | Should |
| FR-026 | Task prioritization with explainable suggestions | Should |
| FR-027 | AI confidence score with "maybe" badge for low confidence | Could |
| FR-028 | User feedback on AI suggestions (thumbs up/down) | Could |

---

## 3. Non-Functional Requirements

### 3.1 Performance

| ID | Requirement |
|---|---|
| NFR-001 | App launch time < 2 seconds on mid-range device |
| NFR-002 | Calendar view renders with 1000+ tasks without lag |
| NFR-003 | Task list scrolls at 60fps with smooth animations |
| NFR-004 | Database queries complete < 100ms |

### 3.2 Reliability

| ID | Requirement |
|---|---|
| NFR-005 | No data loss on app crash |
| NFR-006 | Auto-save task edits every 2 seconds |
| NFR-007 | Graceful handling of recursive task cycles |
| NFR-008 | Notifications reliable after device restart |

### 3.3 Usability

| ID | Requirement |
|---|---|
| NFR-009 | Single-hand friendly UI |
| NFR-010 | Empty states with illustrations |
| NFR-011 | Haptic feedback for key actions |
| NFR-012 | Dark/Light/System theme support |

### 3.4 Maintainability

| ID | Requirement |
|---|---|
| NFR-013 | Test coverage: 70%+ domain, 50%+ ViewModels |
| NFR-014 | Clean Architecture with separated layers |
| NFR-015 | Dependency injection (Hilt) |
| NFR-016 | Interface-based design for extensibility |

### 3.5 Security

| ID | Requirement |
|---|---|
| NFR-017 | Local data encrypted at rest |
| NFR-018 | No hardcoded API keys |

### 3.6 AI-Specific

| ID | Requirement |
|---|---|
| NFR-019 | AI processing async with loading states |
| NFR-020 | Online inference via API with retry logic |
| NFR-021 | AI service interface for swappable providers |
| NFR-022 | API key stored securely |
| NFR-023 | Graceful fallback when AI unavailable |
| NFR-024 | Rate limiting and cost controls |
| NFR-025 | User can disable AI features |

---

## 4. User Stories

### US-001: Create a Task
**As a** user, **I want** to quickly add a task with a title **so that** I can capture to-dos without friction.

**Acceptance Criteria:**
- FAB opens add task screen
- Title required, others optional
- Can set due date, time, priority, tags
- Save creates task and returns to previous screen
- Task appears immediately in relevant lists

### US-002: View Calendar
**As a** user, **I want** to see tasks in a calendar view **so that** I can plan days visually.

**Acceptance Criteria:**
- Horizontal date picker shows 2+ weeks (scrollable)
- Selecting date shows tasks for that day
- Dot indicator on dates with tasks
- Swipe between days updates picker and list
- Today button to jump to current date

### US-003: Add Subtasks
**As a** user, **I want** to break tasks into subtasks **so that** I can track complex work.

**Acceptance Criteria:**
- Can add subtasks from task detail
- Unlimited nesting supported
- Parent shows completion percentage
- Completing parent auto-completes subtasks
- Collapse/expand subtask trees

### US-004: Set Recurring Tasks
**As a** user, **I want** tasks to repeat **so that** I don't recreate routines.

**Acceptance Criteria:**
- Options: Daily, Weekdays, Weekly, Bi-weekly, Monthly, Custom
- Completing generates next instance
- Can edit series or single occurrence
- Skip option available

### US-005: Receive Reminders
**As a** user, **I want** reminders before tasks **so that** I don't miss deadlines.

**Acceptance Criteria:**
- Notification at custom time before due
- Reminder configurable per task
- Tap notification opens task detail
- Snooze or complete from notification

### US-006: Filter and Search
**As a** user, **I want** to find tasks quickly **so that** I can locate what I need.

**Acceptance Criteria:**
- Search by title/description
- Filter chips for priority, tags, date
- Smart lists: Overdue, No Due Date, Completed
- Recent searches shown
- Real-time results

### US-007: Work Offline
**As a** user, **I want** the app offline **so that** I can manage tasks anywhere.

**Acceptance Criteria:**
- All CRUD operations work offline
- Data persists locally
- Sync interface for future cloud
- No errors when offline

### US-008: AI Task Extraction
**As a** user, **I want** to paste text **so that** AI extracts actionable tasks.

**Acceptance Criteria:**
- Text input for pasting content
- AI analyzes and extracts: title, due date, priority
- Shows extracted tasks for review
- User can edit, accept, or reject each
- Batch accept or individual selection

### US-009: AI Calendar Import
**As a** user, **I want** AI to read calendar **so that** it suggests preparatory tasks.

**Acceptance Criteria:**
- Request calendar read permission
- AI analyzes upcoming events
- Suggests context-appropriate tasks
- User reviews and selects which to add
- Links suggested task to calendar event

### US-010: AI Smart Suggestions
**As a** user, **I want** AI to suggest tasks **so that** I don't forget responsibilities.

**Acceptance Criteria:**
- Learns from task history (privacy-first)
- Suggests at appropriate times
- Context-aware (time, day, location)
- User can dismiss, snooze, or accept

### US-011: AI Prioritization
**As a** user, **I want** AI to help prioritize **so that** I know what to focus on.

**Acceptance Criteria:**
- Analyzes due dates, age, patterns, importance
- Suggests "Focus List" (5-7 tasks)
- Explains why each suggested
- User can reorder or remove
- Updates as tasks complete

---

## 5. Screens & UI Structure

### Screen Inventory

| # | Screen | Purpose | Key Elements |
|---|---|---|---|
| 1 | Splash/Onboarding | First launch | App intro, permissions |
| 2 | Home/Dashboard | Main entry | Today's tasks, quick add, stats |
| 3 | Calendar View | Date browsing | Date picker, daily task list |
| 4 | Task List | Filtered views | Filter chips, sort, task cards |
| 5 | Add/Edit Task | Task CRUD | Form, priority, date pickers, tags |
| 6 | Subtask Manager | Nested tasks | Tree view, expand/collapse |
| 7 | Task Detail | Full view | Details, subtasks, activity log |
| 8 | Search | Find tasks | Search bar, filters, results |
| 9 | Settings | Configuration | Theme, notifications, data |
| 10 | Tags/Projects | Manage categories | Tag list, color picker |
| 11 | AI Suggestions | Review AI tasks | Suggestion list, accept/reject |
| 12 | Text Extraction | Paste text for AI | Text input, extract button |

### Navigation

```
Bottom Navigation:
├── Home (Today)
├── Calendar
├── Search
├── [FAB - Add Task]
└── Settings
```

### Key UI Components

| Component | Description |
|---|---|
| Task Card | Swipeable, title, due date, priority, checkbox, subtask indicator |
| Date Picker (Horizontal) | Scrollable dates, highlights today/selected, task dots |
| Priority Chip | Visual indicator (High=Red, Medium=Yellow, Low=Blue) |
| Tag Chip | Rounded colored label |
| Recurrence Indicator | Icon showing pattern |
| Progress Ring | Parent task subtask completion % |
| Empty State | Context-appropriate illustration |

---

## 6. Architecture Overview

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │   Screens   │ │  ViewModels │ │   UI State/Events   │   │
│  │  (Compose)  │ │             │ │                     │   │
│  └─────────────┘ └──────┬──────┘ └─────────────────────┘   │
└─────────────────────────┬───────────────────────────────────┘
                          │ Use Cases (Domain Logic)
┌─────────────────────────┼───────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │   UseCases  │ │   Models    │ │     Repository      │   │
│  │  (Business) │ │  (Entities) │ │    Interfaces       │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │   Local     │ │    Sync     │ │    AI Service       │   │
│  │  (Room DB)  │ │ (Interface) │ │   (Interface)       │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Module Structure

```
app/
├── presentation/
│   ├── screens/
│   ├── components/
│   ├── viewmodels/
│   └── navigation/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── usecase/
│   └── service/
├── data/
│   ├── local/
│   ├── remote/
│   ├── ai/
│   └── repository/
└── di/
```

### Key Interfaces

```kotlin
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(id: String): Result<Unit>
    suspend fun sync(): Result<Unit>
    fun isSyncEnabled(): Boolean
}

interface AIService {
    suspend fun extractTasksFromText(text: String): Result<List<AiTaskSuggestion>>
    suspend fun analyzeCalendarEvents(events: List<CalendarEvent>): Result<List<AiTaskSuggestion>>
    suspend fun suggestPriorities(tasks: List<Task>): Result<List<PrioritySuggestion>>
    fun isAvailable(): Boolean
}
```

---

## 7. Data Models

### Core Entities

```kotlin
data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val parentId: String?,
    val priority: Priority,
    val status: TaskStatus,
    val dueDate: Instant?,
    val dueTime: LocalTime?,
    val tags: List<String>,
    val reminder: Reminder?,
    val recurrence: RecurrenceRule?,
    val subtasks: List<Task>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val completedAt: Instant?,
    val isAiSuggested: Boolean,
    val aiConfidence: Float?,
    val source: TaskSource
)

data class Reminder(
    val type: ReminderType,
    val offsetMinutes: Int?,
    val absoluteTime: Instant?
)

data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int,
    val daysOfWeek: List<DayOfWeek>?,
    val endDate: Instant?,
    val occurrenceCount: Int?
)

data class AiTaskSuggestion(
    val suggestedTitle: String,
    val suggestedDescription: String?,
    val suggestedDueDate: Instant?,
    val suggestedPriority: Priority,
    val confidence: Float,
    val sourceText: String?,
    val sourceEventId: String?
)
```

### Enums

```kotlin
enum class Priority { HIGH, MEDIUM, LOW, NONE }
enum class TaskStatus { PENDING, COMPLETED, ARCHIVED }
enum class TaskSource { MANUAL, AI_TEXT, AI_CALENDAR }
enum class ReminderType { RELATIVE, ABSOLUTE }
enum class Frequency { DAILY, WEEKDAYS, WEEKLY, BIWEEKLY, MONTHLY, CUSTOM }
```

---

## 8. API/Service Interfaces

### AI Service

```kotlin
interface AIService {
    suspend fun extractTasksFromText(text: String): Result<List<AiTaskSuggestion>>
    suspend fun analyzeCalendarEvents(events: List<CalendarEvent>): Result<List<AiTaskSuggestion>>
    suspend fun suggestPriorities(tasks: List<Task>): Result<List<PrioritySuggestion>>
    fun isAvailable(): Boolean
}
```

### Notification Service

```kotlin
interface NotificationService {
    fun scheduleReminder(taskId: String, reminderTime: Instant)
    fun cancelReminder(taskId: String)
    fun showTaskCompletedNotification(task: Task)
}
```

### Calendar Repository

```kotlin
interface CalendarRepository {
    suspend fun getUpcomingEvents(days: Int = 7): List<CalendarEvent>
    suspend fun markEventProcessed(eventId: String, taskIds: List<String>)
}
```

---

## 9. Testing Strategy

| Layer | Test Type | Coverage Target |
|---|---|---|
| Domain (UseCases) | Unit tests | 80%+ |
| ViewModels | Unit tests | 70%+ |
| Repositories | Integration tests | 60%+ |
| Database | Instrumented tests | Core operations |
| UI | Compose UI tests | Critical flows |
| AI Service | Mocked unit tests | Response parsing |

---

## 10. Implementation Phases

### Phase 1: Foundation (Week 1)
- Clean Architecture setup
- Database (Room) setup
- Domain models
- Dependency injection (Hilt)

### Phase 2: Core Features (Week 2-3)
- Create/edit/delete tasks
- Task list views
- Basic UI components

### Phase 3: Advanced Features (Week 4-5)
- Subtasks (unlimited nesting)
- Calendar view
- Reminders & notifications
- Recurring tasks

### Phase 4: AI Integration (Week 6)
- AI service setup
- Text extraction
- Calendar analysis
- Smart suggestions

### Phase 5: Polish (Week 7)
- UI refinements per designs
- Testing
- Performance optimization

---

## 11. Design References

### Primary References

1. **Color Theme & Landing Page**
   - Source: https://cdn.dribbble.com/userupload/6362853/file/original-6683e533d3f303a5118f4c1e6eb60915.png
   - Elements: Orange/peach accent colors, onboarding style

2. **Calendar View Layout**
   - Source: https://dribbble.com/shots/22132415-Task-App-UX-UI
   - Elements: Horizontal date picker + task list below, synced selection

3. **Task Cards & General UI**
   - Source: https://cdn.dribbble.com/userupload/9395640/file/original-ece970185fd4a0e4e99f817ccb0e94eb.png
   - Elements: Card design, typography, spacing

4. **Additional UI Patterns**
   - Source: https://cdn.dribbble.com/userupload/9420743/file/original-e739fd41406d51de8afc7c406ed012d5.jpg
   - Source: https://dribbble.com/shots/19555510-Task-Management-App
   - Elements: Additional screens, interactions

### Design Principles
- Clean, modern aesthetic
- Single-hand friendly
- Meaningful animations
- Accessible contrast ratios
- Consistent spacing and typography

---

## Appendix A: Open Questions

1. AI Provider: Claude API, OpenAI, or other?
2. Monetization: Free with premium features?
3. Widget support for home screen?
4. Wear OS companion app?

---

**Document Approval:**

| Role | Name | Date | Signature |
|---|---|---|---|
| Product Owner | | | |
| Tech Lead | | | |
| Designer | | | |

---

*End of Specification Document*

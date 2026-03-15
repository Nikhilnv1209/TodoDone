# TodoApp - Technical Architecture

## Layered Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                           │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │
│  │     Screens     │  │   ViewModels    │  │    UI Events        │  │
│  │   (Compose UI)  │  │  (State Flow)   │  │   (User Actions)    │  │
│  └────────┬────────┘  └────────┬────────┘  └─────────────────────┘  │
│           │                    │                                     │
│           └────────────────────┘                                     │
│                    │                                                 │
│              UI State (Compose State)                                │
└────────────────────┼────────────────────────────────────────────────┘
                     │
                     ▼ Use Case Calls (suspend functions)
┌─────────────────────────────────────────────────────────────────────┐
│                           DOMAIN LAYER                               │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │
│  │    Use Cases    │  │    Entities     │  │ Repository Ports    │  │
│  │ (Business Logic)│  │  (Task, etc.)   │  │   (Interfaces)      │  │
│  └────────┬────────┘  └─────────────────┘  └─────────────────────┘  │
│           │                                                          │
│           ▼                                                          │
│  ┌─────────────────┐  ┌─────────────────┐                           │
│  │ Service Ports   │  │     Value       │                           │
│  │ (AI, Notification)│   Objects       │                           │
│  └─────────────────┘  └─────────────────┘                           │
└────────────────────┼────────────────────────────────────────────────┘
                     │
                     ▼ Repository Implementation
┌─────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                                │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │
│  │   Local (Room)  │  │  Remote (API)   │  │   AI Service        │  │
│  │  - TaskDao      │  │  (Future Cloud) │  │  - Text extraction  │  │
│  │  - CalendarDao  │  │  - Sync logic   │  │  - Calendar analyze │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────┘  │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐                           │
│  │  DataStore      │  │  SharedPrefs    │                           │
│  │  (Settings)     │  │  (API keys)     │                           │
│  └─────────────────┘  └─────────────────┘                           │
└─────────────────────────────────────────────────────────────────────┘
```

## Package Structure

```
com.example.todoapp/
├── presentation/                    # UI Layer
│   ├── navigation/                  # Navigation graph, routes
│   ├── screens/                     # One folder per screen
│   │   ├── home/
│   │   ├── calendar/
│   │   ├── taskdetail/
│   │   ├── addedit/
│   │   ├── search/
│   │   ├── settings/
│   │   └── ai/
│   ├── components/                  # Reusable UI components
│   │   ├── TaskCard.kt
│   │   ├── DatePicker.kt
│   │   ├── PriorityChip.kt
│   │   └── ...
│   ├── theme/                       # Material 3 theme
│   └── viewmodels/                  # Shared ViewModels
│
├── domain/                          # Business Logic Layer
│   ├── model/                       # Entities
│   │   ├── Task.kt
│   │   ├── Reminder.kt
│   │   ├── RecurrenceRule.kt
│   │   ├── AiSuggestion.kt
│   │   └── enums/
│   ├── repository/                  # Repository interfaces (ports)
│   │   ├── TaskRepository.kt
│   │   ├── CalendarRepository.kt
│   │   └── SettingsRepository.kt
│   ├── service/                     # Service interfaces
│   │   ├── AIService.kt
│   │   └── NotificationService.kt
│   └── usecase/                     # Use cases
│       ├── task/
│       ├── calendar/
│       └── ai/
│
├── data/                            # Data Layer
│   ├── local/                       # Room database
│   │   ├── database/
│   │   ├── entity/
│   │   └── dao/
│   ├── repository/                  # Repository implementations
│   │   ├── TaskRepositoryImpl.kt
│   │   └── ...
│   ├── ai/                          # AI service implementation
│   │   ├── AIServiceImpl.kt
│   │   ├── ClaudeApiClient.kt       # or OpenAI
│   │   └── dto/
│   └── mapper/                      # Entity ↔ Domain mappers
│
└── di/                              # Dependency Injection
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    ├── ServiceModule.kt
    └── AppModule.kt
```

## Key Components

### 1. Domain Layer (Pure Kotlin, No Android Dependencies)

```kotlin
// domain/model/Task.kt
data class Task(
    val id: TaskId,
    val title: String,
    val description: String?,
    val parentId: TaskId?,
    val priority: Priority,
    val status: TaskStatus,
    val dueDate: Instant?,
    val dueTime: LocalTime?,
    val tags: List<Tag>,
    val reminder: Reminder?,
    val recurrence: RecurrenceRule?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val completedAt: Instant?,
    val isAiSuggested: Boolean,
    val aiConfidence: Float?,
    val source: TaskSource
) {
    fun isOverdue(): Boolean =
        status == TaskStatus.PENDING && dueDate?.isBefore(Instant.now()) == true

    fun calculateProgress(subtasks: List<Task>): Float =
        if (subtasks.isEmpty()) if (status == TaskStatus.COMPLETED) 1f else 0f
        else subtasks.count { it.status == TaskStatus.COMPLETED }.toFloat() / subtasks.size
}

// domain/repository/TaskRepository.kt (Port)
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTasksByParent(parentId: TaskId?): Flow<List<Task>>
    fun getTaskById(id: TaskId): Flow<Task?>
    fun searchTasks(query: String): Flow<List<Task>>

    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun deleteTask(id: TaskId): Result<Unit>
    suspend fun completeTask(id: TaskId, completed: Boolean): Result<Task>

    // Future sync support
    suspend fun sync(): Result<SyncResult>
    fun isSyncEnabled(): Boolean
}

// domain/usecase/CreateTaskUseCase.kt
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationService: NotificationService
) {
    suspend operator fun invoke(params: CreateTaskParams): Result<Task> {
        val task = Task.create(params)
        return taskRepository.createTask(task).onSuccess {
            task.reminder?.let { reminder ->
                notificationService.scheduleReminder(task.id, reminder.getTriggerTime())
            }
        }
    }
}
```

### 2. Data Layer

```kotlin
// data/local/entity/TaskEntity.kt
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val parentId: String?,
    val priority: String,
    val status: String,
    val dueDate: Long?,
    val dueTime: String?,
    val tagsJson: String,           // JSON array
    val reminderJson: String?,      // JSON object
    val recurrenceJson: String?,    // JSON object
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?,
    val isAiSuggested: Boolean,
    val aiConfidence: Float?,
    val source: String
)

// data/local/dao/TaskDao.kt
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE parentId IS NULL ORDER BY createdAt DESC")
    fun getRootTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE parentId = :parentId ORDER BY createdAt DESC")
    fun getSubtasks(parentId: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE dueDate >= :startOfDay AND dueDate < :endOfDay
        ORDER BY priority DESC, createdAt DESC
    """)
    fun getTasksForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
}

// data/repository/TaskRepositoryImpl.kt
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getRootTasks()
            .map { entities -> entities.map { taskMapper.toDomain(it) } }

    override suspend fun createTask(task: Task): Result<Task> = runCatching {
        taskDao.insertTask(taskMapper.toEntity(task))
        task
    }

    override suspend fun sync(): Result<SyncResult> {
        // Interface prepared for future implementation
        return Result.success(SyncResult.NoOp)
    }

    override fun isSyncEnabled(): Boolean = false
}

// data/ai/AIServiceImpl.kt
class AIServiceImpl @Inject constructor(
    private val apiClient: ClaudeApiClient,
    private val contextProvider: ContextProvider
) : AIService {

    override suspend fun extractTasksFromText(text: String): Result<List<AiTaskSuggestion>> =
        runCatching {
            val prompt = buildExtractionPrompt(text)
            val response = apiClient.sendMessage(prompt)
            parseTaskSuggestions(response)
        }

    override suspend fun analyzeCalendarEvents(
        events: List<CalendarEvent>
    ): Result<List<AiTaskSuggestion>> = runCatching {
        val prompt = buildCalendarAnalysisPrompt(events)
        val response = apiClient.sendMessage(prompt)
        parseTaskSuggestions(response)
    }

    override fun isAvailable(): Boolean = apiClient.isConfigured()
}
```

### 3. Presentation Layer

```kotlin
// presentation/screens/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayTasks: GetTodayTasksUseCase,
    private val completeTask: CompleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayTasks()
    }

    private fun loadTodayTasks() {
        getTodayTasks()
            .onEach { tasks ->
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            }
            .catch { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onTaskComplete(taskId: TaskId, completed: Boolean) {
        viewModelScope.launch {
            completeTask(taskId, completed)
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}

// presentation/screens/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (TaskId) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.tasks.isEmpty() -> EmptyState()
            else -> TaskList(
                tasks = uiState.tasks,
                onTaskComplete = viewModel::onTaskComplete,
                onTaskClick = onNavigateToTaskDetail,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
```

## Dependency Injection Setup

```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        )
        .addMigrations(/* migrations */)
        .build()
    }

    @Provides
    fun provideTaskDao(database: TodoDatabase): TaskDao = database.taskDao()
}

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    abstract fun bindAIService(
        impl: AIServiceImpl
    ): AIService
}
```

## Data Flow

### Read Flow (e.g., Load Tasks)
```
UI (Screen)
  ↓ observe
ViewModel
  ↓ collect Flow
Use Case (optional transform)
  ↓ call
Repository Interface
  ↓ implement
Repository Implementation
  ↓ query
DAO / API
  ↓ return
Database / Network
  ↓ map
Entity → Domain Model
  ↓ emit
Flow → ViewModel → UI State → Compose Recomposition
```

### Write Flow (e.g., Create Task)
```
UI Event (onClick)
  ↓
ViewModel
  ↓ launch
Use Case
  ↓ validate
Business Rules
  ↓ call
Repository Interface
  ↓ implement
Repository Implementation
  ↓ insert
DAO / API
  ↓ result
Success / Failure
  ↓ update
UI State / Show Error
```

## Error Handling Strategy

```kotlin
// domain/util/Result.kt
typealias Result<T> = kotlin.Result<T>

// Extension for UI-friendly error mapping
fun <T> Result<T>.toUiState(
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit
) {
    onSuccess { data -> onSuccess(data) }
    onFailure { error -> onError(error.userFriendlyMessage()) }
}

// Error types
todoapp.error.AppError
├── DatabaseError(message: String)
├── NetworkError(message: String, isRecoverable: Boolean)
├── AIError(message: String, isTemporary: Boolean)
├── ValidationError(field: String, message: String)
└── UnknownError(throwable: Throwable)
```

## Testing Strategy

### Unit Tests (Domain Layer)
```kotlin
@Test
fun `create task with empty title should fail`() = runTest {
    val result = createTaskUseCase(
        CreateTaskParams(title = "")
    )
    assertTrue(result.isFailure)
    assertIs<ValidationError>(result.exceptionOrNull())
}

@Test
fun `overdue task should return true when past due date`() {
    val task = Task.create(
        title = "Test",
        dueDate = Instant.now().minus(1, ChronoUnit.DAYS)
    )
    assertTrue(task.isOverdue())
}
```

### Integration Tests (Data Layer)
```kotlin
@Test
fun `task inserted should be retrievable`() = runTest {
    val task = Task.create(title = "Test Task")
    repository.createTask(task)

    val retrieved = repository.getTaskById(task.id).first()
    assertEquals(task.title, retrieved?.title)
}
```

### UI Tests (Presentation Layer)
```kotlin
@Test
fun `clicking add task should navigate to add screen`() {
    composeTestRule.setContent { HomeScreen(...) }

    composeTestRule.onNodeWithContentDescription("Add Task").performClick()

    verify { navigator.navigateToAddTask() }
}
```

## Performance Considerations

1. **Database**: Use `@Transaction` for nested inserts, index on frequently queried columns
2. **Flows**: Use `distinctUntilChanged()` to prevent unnecessary recompositions
3. **Images**: Coil for async image loading with caching
4. **AI Calls**: Debounce user input, cache results, show loading states
5. **Lists**: LazyColumn with keys for stable item identity

## Security

```kotlin
// API keys stored encrypted
val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// Database encryption (optional)
Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db")
    .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes("passphrase".toCharArray())))
    .build()
```

---

*Architecture Version: 1.0*
*Last Updated: 2025-03-15*

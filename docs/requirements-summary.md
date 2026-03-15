# TodoApp - Requirements Summary

## Quick Reference

### Must-Have (P0)
- [ ] Create/edit/delete tasks with title, description, due date, priority, tags
- [ ] Unlimited task nesting (subtasks)
- [ ] Calendar view with horizontal date picker + task list
- [ ] Task list views: All, Today, Tomorrow, Upcoming, Someday
- [ ] Due dates with time and custom reminders
- [ ] Recurring tasks (Daily, Weekly, Monthly, Custom)
- [ ] Smart lists: Overdue, No Due Date, Recently Completed
- [ ] Search and filter by priority/tags
- [ ] Local persistence (Room)
- [ ] Clean Architecture with sync interfaces

### Should-Have (P1)
- [ ] AI text extraction (paste notes → tasks)
- [ ] AI calendar analysis (events → preparatory tasks)
- [ ] AI task prioritization suggestions
- [ ] Subtask completion contributes to parent progress
- [ ] Sort options (Due date, Priority, Created, Manual)
- [ ] Data export/import

### Could-Have (P2)
- [ ] Archive completed tasks after 30 days
- [ ] AI pattern learning for suggestions
- [ ] AI confidence scoring
- [ ] User feedback on AI suggestions
- [ ] Widget support
- [ ] Wear OS companion

---

## Key Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Architecture | Clean Architecture | Scalable, testable, supports future cloud sync |
| UI Framework | Jetpack Compose | Modern, declarative, efficient |
| Database | Room | Type-safe SQL, coroutines support |
| DI Framework | Hilt | Standard Android solution |
| Data Strategy | Local-first + sync interfaces | Offline functionality, future cloud ready |
| AI Strategy | Online API inference | More capable than on-device, provider flexibility |

---

## Success Metrics

1. **Performance**: Launch < 2s, 60fps scroll, DB queries < 100ms
2. **Quality**: 70%+ domain test coverage, 50%+ ViewModel coverage
3. **User Experience**: Single-hand friendly, meaningful empty states
4. **Reliability**: No data loss, reliable notifications

---

## Out of Scope (v1.0)

- Cloud sync implementation (interfaces only)
- Collaboration/sharing
- Attachments/file uploads
- Location-based reminders
- Web/desktop clients
- Third-party integrations (Slack, Trello, etc.)

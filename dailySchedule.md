# Daily Schedule System - How It Works

## Overview

The daily schedule system uses a **template-based approach with override capabilities**. This allows for a default daily routine with the flexibility to handle special days (Poya days, holidays, etc.).

---

## The 5 Tables

### 1. `activity`
Master list of all reusable activities.

**Columns:**
- `activity_id` - Primary key
- `title` - Activity name (e.g., "Morning Meditation", "Breakfast")
- `description` - Detailed description
- `media_url` - Optional image/video URL

**Purpose:** Defines the building blocks that can be used in both templates and overrides.

---

### 2. `schedule_template`
Defines reusable schedule patterns.

**Columns:**
- `template_id` - Primary key
- `name` - Template name (e.g., "Daily Routine")
- `description` - Description of the template
- `is_active` - Boolean flag (only ONE should be active at a time)

**Purpose:** Represents the default daily schedule pattern.

**Note:** Since we have only one meditation program, only one template should be active at any time.

---

### 3. `template_schedule_activity`
Links activities to templates with specific times.

**Columns:**
- `id` - Primary key
- `template_id` - Foreign key to `schedule_template`
- `activity_id` - Foreign key to `activity`
- `start_time` - Start time (TIME type)
- `end_time` - End time (TIME type)
- `notes` - Optional notes

**Purpose:** Defines what activities happen at what times in the default template.

**Example:**
```
For template 1:
- Activity 1 (Morning Meditation) at 05:00 - 06:30
- Activity 2 (Breakfast) at 07:00 - 08:00
- Activity 3 (Dharma Talk) at 09:00 - 10:30
```

---

### 4. `schedule_override`
Marks specific dates that have custom schedules.

**Columns:**
- `override_id` - Primary key
- `override_date` - The date (UNIQUE - one override per date)

**Purpose:** Acts as a flag indicating "this date has a special schedule".

**Important:** If a row exists for a specific date, the system ignores the template and uses the override schedule.

---

### 5. `override_activity`
Contains the FULL schedule for override dates.

**Columns:**
- `id` - Primary key
- `override_id` - Foreign key to `schedule_override`
- `activity_id` - Foreign key to `activity`
- `start_time` - Start time (TIME type)
- `end_time` - End time (TIME type)
- `is_cancelled` - Boolean flag to mark cancelled activities
- `notes` - Optional notes for this specific occurrence

**Purpose:** Stores the complete schedule for the override date.

**Critical Rule:** If even ONE activity is different on a specific day, you store the **ENTIRE day's schedule** in this table (not just the changed activities).

---

## Query Logic

### When fetching a schedule for a specific date:

```
1. Check if override exists:
   SELECT * FROM schedule_override WHERE override_date = ?

2a. IF OVERRIDE EXISTS:
    → Fetch FULL schedule from override_activity table
    → Ignore template completely

2b. IF NO OVERRIDE:
    → Fetch FULL schedule from template_schedule_activity table
    → Use the active template
```

---

## Example Flow

### Scenario: Get schedule for December 15, 2025

**Query:**
```sql
SELECT * FROM schedule_override WHERE override_date = '2025-12-15';
```

**Case 1: Override EXISTS (Poya Day)**
```sql
-- Fetch from override_activity
SELECT a.*, oa.start_time, oa.end_time, oa.is_cancelled, oa.notes
FROM override_activity oa
JOIN activity a ON oa.activity_id = a.activity_id
WHERE oa.override_id = (SELECT override_id FROM schedule_override WHERE override_date = '2025-12-15')
ORDER BY oa.start_time;
```

**Result:**
- 04:00 - 07:00: Morning Meditation (extended)
- 07:30 - 08:30: Breakfast
- 09:00 - 10:30: Dharma Talk (CANCELLED - is_cancelled = true)
- 14:00 - 15:00: Walking Meditation
- 17:00 - 20:00: Evening Chanting (extended)

---

**Case 2: NO Override (Normal Day)**
```sql
-- Fetch from template_schedule_activity
SELECT a.*, tsa.start_time, tsa.end_time, tsa.notes
FROM template_schedule_activity tsa
JOIN activity a ON tsa.activity_id = a.activity_id
WHERE tsa.template_id = (SELECT template_id FROM schedule_template WHERE is_active = true)
ORDER BY tsa.start_time;
```

**Result:**
- 05:00 - 06:30: Morning Meditation
- 07:00 - 08:00: Breakfast
- 09:00 - 10:30: Dharma Talk
- 14:00 - 15:00: Walking Meditation
- 18:00 - 19:00: Evening Chanting

---

## The "All or Nothing" Rule

**Key Principle:** Override dates contain the **COMPLETE** schedule for that day.

### Why?

✅ **Simpler Queries** - No need to merge template + override
✅ **Clear Separation** - Either ALL from template OR ALL from override
✅ **No Partial States** - Avoids bugs from mixing sources
✅ **Predictable** - Easy to understand what schedule applies

### What this means:

If you want to change just ONE activity on a special day:
1. Copy ALL activities from the template
2. Modify the one you want to change
3. Store the ENTIRE schedule in `override_activity`

---

## Summary

| Table | Purpose |
|-------|---------|
| `activity` | Master list of activities (reusable) |
| `schedule_template` | Default schedule pattern definition |
| `template_schedule_activity` | Default daily schedule (activity times) |
| `schedule_override` | Flags for dates with special schedules |
| `override_activity` | FULL schedule for override dates |

**Flow:**
```
User Query → Check override table → Found? → Use override_activity
                                   → Not Found? → Use template_schedule_activity
```

---

## Design Benefits

✅ **Efficient** - Don't duplicate schedule for every single day
✅ **Flexible** - Easy to handle special days without touching defaults
✅ **Clean** - Activities are reusable across templates and overrides
✅ **Scalable** - Can support multiple templates in the future if needed

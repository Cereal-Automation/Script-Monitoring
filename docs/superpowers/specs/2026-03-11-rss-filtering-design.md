# Design: RSS Filtering Configuration

**Date:** 2026-03-11  
**Status:** Approved

## Overview

Extend the `script-rss` module configuration to allow users to filter new RSS items based on `author`, `categories`, and `keywords` (in title/description). Users can configure the logical operator between multiple filters (AND vs. OR).

## Data Extraction (`RssFeedItemRepository`)

The existing `RssFeedItemRepository` currently extracts `title`, `description`, `link`, and `pubDate`. 
It needs to be updated to extract `author` and `categories` from the underlying `RssItem` model and map them to the Cereal SDK's `Item` model as `ItemProperty.Custom` properties. Multiple categories should be mapped as separate `ItemProperty.Custom` instances with the same key (e.g., `ItemProperty.Custom("category", "Tech")`).

## Configuration (`RssConfiguration`)

Add the following configurable fields:

| Config key           | UI Name              | Type          | Notes                                                                 |
|----------------------|----------------------|---------------|-----------------------------------------------------------------------|
| `filter_keywords`    | Keywords             | `String?`     | Comma-separated list of keywords to search for in title or description|
| `filter_authors`     | Authors              | `String?`     | Comma-separated list of author names to match                         |
| `filter_categories`  | Categories           | `String?`     | Comma-separated list of categories to match                           |
| `filter_logic`       | Filter Logic         | `FilterLogic` | Enum: `MATCH_ALL` (AND) or `MATCH_ANY` (OR). Default: `MATCH_ANY`      |

*Note: If no filters are provided, the strategy behaves identically to the original "notify on every new item" behavior.*

## Strategy (`FilteredNewItemMonitorStrategy`)

Create a new `MonitorStrategy` named `FilteredNewItemMonitorStrategy` and explicitly place it in the `script-rss` module so the RSS-specific logic remains encapsulated within the script that uses it. This strategy encapsulates both the "new item" check and the filtering logic.

1. **Baseline Check:** Uses **composition** by wrapping an instance of `NewItemAvailableMonitorStrategy` to delegate the "newness" baseline check. It should only execute its custom filtering logic if the underlying strategy determines the item is actually new (i.e. if the underlying strategy returns a non-null notification message).
2. **Filter Evaluation:**
    *   **Missing Fields:** If a filter is configured but the incoming RSS item *does not have* that field at all, it implicitly fails the check for that specific filter.
    *   **Keywords:** Checks if any of the configured keywords exist in the item's `name` or `description` (case-insensitive).
    *   **Authors:** Checks if the item's `author` custom property exactly matches any configured authors (case-insensitive).
    *   **Categories:** Checks if any of the item's `categories` custom properties exactly match any configured categories (case-insensitive).
3. **Logical Operation:** 
    *   If `MATCH_ALL`, the item must pass *every* configured filter that is not blank.
    *   If `MATCH_ANY`, the item must pass *at least one* configured filter that is not blank.
4. **Notification:** If the item passes the filters, return a formatted notification string (e.g., `"{title} is available"`). If it fails, return `null`.

## Script Registration (`RssScript`)

Update `RssScript.kt` to:
1. Parse the comma-separated strings into `List<String>`. The resulting strings must be trimmed of whitespace. Any empty or blank strings resulting from the split should be filtered out and dropped.
2. Retrieve the `FilterLogic` enum value.
3. Replace the usage of `NewItemAvailableMonitorStrategy` with `FilteredNewItemMonitorStrategy`, injecting the parsed filters and logic.
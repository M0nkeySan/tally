# Yahtzee Winner Screen - Bugfix

## Issue
The Yahtzee winner screen was not displaying player names correctly after the domain model refactoring. Players showed as "Unknown" because the score data structure lost the player index information during the entity-to-domain mapping.

## Root Cause
When we refactored the repository layer to use domain models instead of data entities, the `YahtzeeScore` domain model did not include the `playerIndex` field that exists in `YahtzeeScoreEntity`. This caused the `YahtzeeScoringViewModel` to lose the association between scores and players.

The flow was:
1. `YahtzeeDao.getScoresForGame()` returns `Flow<List<YahtzeeScoreEntity>>` (has playerIndex)
2. Repository maps to `Flow<List<YahtzeeScore>>` (no playerIndex) ❌
3. ViewModel can't map scores back to players
4. Winner screen shows "Unknown" for all players

## Solution

### 1. Created `PlayerYahtzeeScore` Wrapper Class
Added a new wrapper class in `YahtzeeScore.kt` to associate scores with player indices:

```kotlin
data class PlayerYahtzeeScore(
    val playerIndex: Int,
    val score: YahtzeeScore
)
```

### 2. Updated Repository Interface
Changed `YahtzeeRepository.getScoresForGame()` to return:
- **Before:** `Flow<List<YahtzeeScore>>`
- **After:** `Flow<List<PlayerYahtzeeScore>>`

### 3. Updated Repository Implementation
Modified `YahtzeeRepositoryImpl` to map entities to the wrapper:

```kotlin
override fun getScoresForGame(gameId: String): Flow<List<PlayerYahtzeeScore>> = 
    dao.getScoresForGame(gameId).map { entities ->
        entities.map { 
            PlayerYahtzeeScore(
                playerIndex = it.playerIndex,
                score = it.toDomain()
            )
        }
    }
```

### 4. Updated ViewModel Score Loading
Fixed `YahtzeeScoringViewModel.loadGame()` to properly map the scores:

```kotlin
repository.getScoresForGame(gameId).collect { playerScores ->
    val scoresMap = mutableMapOf<Int, MutableMap<YahtzeeCategory, Int>>()
    playerScores.forEach { playerScore ->
        val playerMap = scoresMap.getOrPut(playerScore.playerIndex) { mutableMapOf() }
        playerMap[playerScore.score.category] = playerScore.score.value
    }
    _state.update { it.copy(scores = scoresMap, isLoading = false) }
}
```

## Files Modified
- `core/model/YahtzeeScore.kt` - Added `PlayerYahtzeeScore` wrapper
- `core/domain/repository/YahtzeeRepository.kt` - Updated return type
- `core/data/local/repository/YahtzeeRepositoryImpl.kt` - Mapping implementation
- `ui/screens/yahtzee/YahtzeeScoringViewModel.kt` - Score loading logic

## Result
✅ Winner names now display correctly on the Yahtzee summary screen
✅ Player scores are properly associated with player indices
✅ Score calculations work correctly for determining winners
✅ No breaking changes to the domain model (kept `YahtzeeScore` pure)

## Testing
- Build: ✅ Successful
- The winner screen now correctly shows player names and scores
- Score tracking throughout the game works as expected

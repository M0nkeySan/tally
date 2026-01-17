package io.github.m0nkeysan.gamekeeper.ui.strings

/**
 * French string resources for the GameKeeper application.
 * Implements [StringProvider] for French language support.
 *
 * Translation notes:
 * - Game names (Yahtzee, Tarot) kept in English
 * - French card game terms kept as-is (Petit au bout, Poignée, Chelem)
 * - Emojis preserved unchanged
 * - Format strings maintain placeholder order (%d, %s)
 */
object AppStringsFr : StringProvider {
    // Home Screen
    override val HOME_TITLE = "GameKeeper"

    // Counter Screen
    override val COUNTER_TITLE = "Compteur"
    override val COUNTER_SETTINGS = "Paramètres"
    override val COUNTER_REINITIALIZE_ALL = "Réinitialiser tout"
    override val COUNTER_DELETE_EVERYTHING = "Supprimer tout"
    override val COUNTER_EMPTY_STATE = "Appuyez sur + pour ajouter un compteur"
    override val COUNTER_REMOVE_LABEL = "SUPPRIMER (-)"
    override val COUNTER_ADD_LABEL = "AJOUTER (+)"

    // Player Selection Screen
    override val PLAYERS_NO_PLAYERS = "Aucun joueur trouvé. Ajoutez-en un !"

    // Common Actions
    override val ACTION_DELETE = "Supprimer"
    override val ACTION_DELETE_ALL = "Tout supprimer"
    override val ACTION_CANCEL = "Annuler"
    override val ACTION_SAVE = "Enregistrer"
    override val ACTION_BACK = "Retour"
    override val ACTION_CREATE = "Créer"
    override val ACTION_RESET = "Réinitialiser"

    // Loading and States
    override val STATE_LOADING = "Chargement..."
    override val STATE_LOADING_GAMES = "Chargement des jeux..."

    // Game Specific
    override val GAME_TAROT = "Tarot"
    override val GAME_YAHTZEE = "Yahtzee"
    override val GAME_FINGER_SELECTOR = "Sélecteur de doigts"
    override val GAME_COUNTER = "Compteur"

    // Descriptions
    override val DESC_TAROT = "Inscrivez les points des parties de Tarot pour 3, 4 ou 5 joueurs"
    override val DESC_YAHTZEE = "Complétez la feuille de pointage Yahtzee avec bonus automatiques"
    override val DESC_COUNTER = "Compteur simple pour n'importe quel jeu de société"

    // Game Operations
    override val GAME_DELETE_ALL_TITLE = "Supprimer tous les jeux"
    override val GAME_DELETE_ALL_CONFIRM =
        "Êtes-vous sûr de vouloir supprimer tous les jeux ? Cette action est irréversible."
    override val GAME_CREATE = "Créer un jeu"

    // Dice Roller
    override val GAME_DICE = "Lanceur de dés"
    override val DESC_DICE = "Lancez des dés personnalisés pour n'importe quel jeu de société"

    // Common Actions (extended)
    override val ACTION_RETRY = "Réessayer"
    override val ACTION_OK = "OK"

    // Common Content Descriptions (Accessibility)
    override val CD_BACK = "Retour"
    override val CD_SETTINGS = "Paramètres"
    override val CD_ADD = "Ajouter"
    override val CD_MENU = "Menu"
    override val CD_ADD_PLAYER = "Ajouter un joueur"
    override val CD_TOGGLE_COLLAPSE = "Réduire"
    override val CD_TOGGLE_EXPAND = "Développer"
    override val CD_PLAYER = "Avatar du joueur"
    override val CD_GAME_ICON = "Icône du jeu"
    override val CD_YAHTZEE_ICON = "Yahtzee"
    override val CD_TAROT_ICON = "Tarot"
    override val CD_FINISHED_GAME = "Jeu terminé"
    override val CD_REMOVE_PLAYER = "Supprimer le joueur"

    // Common Dialogs
    override val DIALOG_DELETE_ALL_TITLE = "Tout supprimer"
    override val DIALOG_DELETE_COUNTER_TITLE = "Supprimer le compteur"
    override val DIALOG_DELETE_COUNTER_MESSAGE =
        "Êtes-vous sûr de vouloir supprimer ce compteur ? Cette action est irréversible."
    override val DIALOG_DEACTIVATE_PLAYER = "Désactiver"
    override val DIALOG_DELETE_PLAYER = "Supprimer"

    // Tarot - Statistics
    override val TAROT_STATS_TAB_CURRENT_GAME = "Partie actuelle"
    override val TAROT_STATS_TAB_PLAYER_STATS = "Statistiques des joueurs"
    override val TAROT_STATS_LABEL_CALLED = "Appelé"
    override val TAROT_STATS_LABEL_AS_TAKER = "En tant que preneur"
    override val TAROT_STATS_LABEL_WIN_RATE = "Taux de victoire"
    override val TAROT_STATS_LABEL_FAVORITE_BID = "Enchère préférée :"
    override val TAROT_STATS_LABEL_MOST_SUCCESSFUL = "Plus réussi :"
    override val TAROT_STATS_SECTION_PERFORMANCE_DETAILS = "Détails des performances"
    override val TAROT_STATS_LABEL_AVG_BOUTS = "Moy. Bouts/Manche"
    override val TAROT_STATS_LABEL_AVG_WON = "Moy. Remportée"
    override val TAROT_STATS_LABEL_AVG_LOST = "Moy. Perdue"
    override val TAROT_STATS_SECTION_CALLED_PERFORMANCE = "Performance appelée"
    override val TAROT_STATS_ERROR_TITLE = "Oups ! Quelque chose s'est mal passé"

    // Tarot - Scoring
    override val TAROT_SCORING_SCREEN_TITLE = "Pointage de la partie"
    override val TAROT_SCORING_CD_ADD_ROUND = "Ajouter une manche"
    override val TAROT_SCORING_SECTION_SCORES = "Points"
    override val TAROT_SCORING_SECTION_HISTORY = "Historique"
    override val TAROT_SCORING_EMPTY_ROUNDS = "Aucune manche pour le moment"
    override val TAROT_SCORING_ROUND_TITLE = "Manche %d - %s"
    override val TAROT_SCORING_ROUND_DETAILS = "%s • %d bouts • %d pts"
    override val TAROT_SCORING_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
    override val TAROT_SCORING_ANNOUNCE_POIGNEE = "Poignée %s"
    override val TAROT_SCORING_ANNOUNCE_CHELEM = "Chelem : %s"
    override val TAROT_SCORING_GAME_TITLE = "Parties de Tarot"

    // Tarot - Round Addition
    override val TAROT_ROUND_ACTION_SAVE = "ENREGISTRER LA MANCHE"
    override val TAROT_ROUND_SECTION_PLAYERS = "JOUEURS"
    override val TAROT_ROUND_LABEL_TAKER = "PRENEUR"
    override val TAROT_ROUND_LABEL_CALLED_PLAYER = "JOUEUR APPELÉ"
    override val TAROT_ROUND_SECTION_BID = "ENCHÈRE"
    override val TAROT_ROUND_SECTION_BOUTS = "BOUTS"
    override val TAROT_ROUND_LABEL_BOUTS = "%d Bouts"
    override val TAROT_ROUND_SECTION_POINTS = "POINTS"
    override val TAROT_ROUND_FIELD_ATTACKER_SCORE = "Attaquant"
    override val TAROT_ROUND_LABEL_DEFENSE = "DÉFENSE"
    override val TAROT_ROUND_CONTRACT_WON = "CONTRAT GAGNÉ (+%d pts)"
    override val TAROT_ROUND_CONTRACT_LOST = "CONTRAT PERDU (%d pts)"
    override val TAROT_ROUND_SECTION_ANNOUNCES = "ANNONCES"
    override val TAROT_ROUND_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
    override val TAROT_ROUND_ANNOUNCE_POIGNEE = "Poignée"
    override val TAROT_ROUND_LABEL_CHELEM = "Chelem"

    // Counter - Main Screen
    override val COUNTER_CD_ADD = "Ajouter un compteur"
    override val COUNTER_CD_HISTORY = "Historique"
    override val COUNTER_CD_RESET_ALL = "Réinitialiser tous les compteurs"
    override val COUNTER_CD_DELETE_ALL = "Supprimer tous les compteurs"
    override val COUNTER_DIALOG_SETTINGS_TITLE = "Paramètres du compteur"
    override val COUNTER_DIALOG_SETTINGS_LABEL = "Mettre en évidence le joueur avec :"
    override val COUNTER_SETTINGS_OPTION_MOST = "📈 Plus de points"
    override val COUNTER_SETTINGS_OPTION_LEAST = "📉 Moins de points"
    override val COUNTER_ACTION_CLOSE = "FERMER"
    override val COUNTER_DIALOG_RESET_TITLE = "Réinitialiser tous les compteurs"
    override val COUNTER_DIALOG_RESET_MESSAGE =
        "Êtes-vous sûr de vouloir réinitialiser tous les compteurs à 0 ?"
    override val COUNTER_DIALOG_DELETE_TITLE = "Supprimer tout"
    override val COUNTER_DIALOG_DELETE_MESSAGE =
        "Êtes-vous sûr de vouloir supprimer tous les compteurs ? Cette action est irréversible."
    override val COUNTER_ACTION_DELETE = "Supprimer tout"
    override val COUNTER_DIALOG_ADJUST_LABEL = "AJUSTEMENT MANUEL"
    override val COUNTER_DIALOG_ADJUST_PLACEHOLDER = "0"
    override val COUNTER_DIALOG_SET_SCORE_LABEL = "DÉFINIR NOUVEAU SCORE"
    override val COUNTER_CD_DECREASE = "Diminuer"
    override val COUNTER_CD_INCREASE = "Augmenter"
    override val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"

    // Counter - History & Edit
    override val COUNTER_HISTORY_TITLE = "Historique"
    override val COUNTER_HISTORY_SUBTITLE = "⚠️ L'historique est effacé à la fermeture de l'application"
    override val COUNTER_HISTORY_CD_DELETE = "Supprimer tous les historiques"
    override val COUNTER_HISTORY_EMPTY = "Aucun changement de compteur pour le moment"
    override val COUNTER_HISTORY_DELETED_EMOJI = "🗑️"
    override val COUNTER_HISTORY_DELETED_TEXT = "Supprimé"
    override val COUNTER_EDIT_TITLE = "Modifier le compteur"
    override val COUNTER_EDIT_CD_DELETE = "Supprimer"
    override val COUNTER_EDIT_ACTION_SAVE = "ENREGISTRER LES MODIFICATIONS"
    override val COUNTER_EDIT_LABEL_COLOR = "COULEUR D'ACCENTUATION"
    override val COUNTER_EDIT_FIELD_NAME = "NOM"
    override val COUNTER_EDIT_PLACEHOLDER_NAME = "Nom du joueur"
    override val COUNTER_EDIT_FIELD_VALUE = "VALEUR"
    override val COUNTER_EDIT_PLACEHOLDER_VALUE = "0"

    // Yahtzee - Scoring
    override val YAHTZEE_SCORING_CD_PREVIOUS = "Joueur précédent"
    override val YAHTZEE_SCORING_TURN_INDICATOR = "●"
    override val YAHTZEE_SCORING_FALLBACK_NAME = "Inconnu"
    override val YAHTZEE_SCORING_CD_DROPDOWN = "Liste déroulante de sélection du joueur"
    override val YAHTZEE_SCORING_TOTAL_FORMAT = "Total : %d"
    override val YAHTZEE_SCORING_CD_NEXT = "Joueur suivant"
    override val YAHTZEE_SCORING_TURN_LABEL = "À votre tour"
    override val YAHTZEE_SCORING_VIEWING_LABEL = "Consultation de la feuille de %s"
    override val YAHTZEE_SECTION_UPPER = "Section supérieure"
    override val YAHTZEE_SECTION_LOWER = "Section inférieure"
    override val YAHTZEE_DIALOG_SELECT_SCORE = "Sélectionnez le score pour %s"
    override val YAHTZEE_DIALOG_ENTER_SCORE = "Entrez le score pour %s"
    override val YAHTZEE_PLACEHOLDER_DICE_SUM = "Somme des dés"
    override val YAHTZEE_ERROR_SCORE_TOO_HIGH = "Le score ne peut pas dépasser 30"
    override val YAHTZEE_LABEL_UPPER_BONUS = "Bonus supérieur (63+)"
    override val YAHTZEE_LABEL_BONUS_NEEDED = "-%d"
    override val YAHTZEE_LABEL_BONUS_EARNED = "+35"
    override val YAHTZEE_LABEL_TOTAL_SCORE = "SCORE TOTAL"
    override val YAHTZEE_GAME_TITLE = "Parties de Yahtzee"
    override val YAHTZEE_NEW_GAME_TITLE = "Nouvelle partie de Yahtzee"
    override val YAHTZEE_GAME_NAME_DEFAULT = "Partie de Yahtzee"

    // Dice Roller
    override val DICE_DISPLAY_FORMAT = "%d × d%d"
    override val DICE_ROLLS_FORMAT = "Lancers : %s"
    override val DICE_TOTAL_FORMAT = "Total : %d"
    override val DICE_INSTRUCTION_TAP = "Appuyez n'importe où pour lancer. Appui long pour les paramètres."
    override val DICE_DIALOG_TITLE = "Paramètres des dés"
    override val DICE_FIELD_NUMBER = "Nombre de dés"
    override val DICE_FIELD_TYPE = "Type de dé"
    override val DICE_FIELD_CUSTOM = "Dé personnalisé"
    override val DICE_ERROR_NOT_VALID = "Doit être un nombre valide"
    override val DICE_ERROR_MIN_SIDES = "Le minimum est %d faces"
    override val DICE_ERROR_MAX_SIDES = "Le maximum est %d faces"
    override val DICE_PLACEHOLDER_CUSTOM = "2-99"
    override val DICE_SETTING_ANIMATION = "Animation"
    override val DICE_SETTING_SHAKE = "Secouer pour lancer"

    // Finger Selector
    override val FINGER_SELECTOR_DIALOG_TITLE = "Paramètres du sélecteur"
    override val FINGER_SELECTOR_SECTION_MODE = "Mode"
    override val FINGER_SELECTOR_MODE_FINGERS = "Doigts"
    override val FINGER_SELECTOR_MODE_GROUPS = "Groupes"
    override val FINGER_SELECTOR_CD_TOUCH = "Touchez ici pour placer le doigt"
    override val FINGER_SELECTOR_INSTRUCTION_PLACE = "Placez vos doigts"
    override val FINGER_SELECTOR_INSTRUCTION_WAIT = "Attendre l'attribution des groupes"
    override val FINGER_SELECTOR_SLIDER_VALUE_FORMAT = "%s : %d"

    // Game Selection & Creation
    override val GAME_SELECTION_CD_STATISTICS = "Statistiques"
    override val GAME_SELECTION_CD_CREATE = "Créer un nouveau jeu"
    override val GAME_SELECTION_CD_DELETE_ALL = "Supprimer tous les jeux"
    override val GAME_SELECTION_LOADING = "Chargement des jeux..."
    override val GAME_SELECTION_EMPTY = "Aucun jeu pour le moment. Créez-en un !"
    override val GAME_SELECTION_CD_DELETE_GAME = "Supprimer le jeu"
    override val GAME_CREATION_ACTION_CANCEL = "Annuler"
    override val GAME_CREATION_ACTION_CREATE = "Créer un jeu"
    override val GAME_CREATION_NEW_TAROT_TITLE = "Nouvelle partie de Tarot"
    override val GAME_CREATION_TAROT_NAME_DEFAULT = "Partie de Tarot"
    override val GAME_CREATION_FIELD_GAME_NAME = "Nom du jeu"
    override val GAME_DELETION_DIALOG_TAROT_TITLE = "Supprimer le jeu"
    override val GAME_DELETION_DIALOG_TAROT_MESSAGE =
        "Êtes-vous sûr de vouloir supprimer le jeu '%s' ? Toutes les manches seront également supprimées."
    override val GAME_DELETION_DIALOG_YAHTZEE_TITLE = "Supprimer le jeu"
    override val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE =
        "Êtes-vous sûr de vouloir supprimer '%s' ? Tous les scores seront perdus."

    // Player Management
    override val PLAYER_SECTION_PLAYERS = "Joueurs"
    override val PLAYER_SECTION_DEACTIVATED = "Désactivés"
    override val PLAYER_FIELD_NAME = "NOM"
    override val PLAYER_PLACEHOLDER_NAME = "Nom du joueur"
    override val PLAYER_ERROR_NAME_TAKEN = "Ce nom est déjà pris"
    override val PLAYER_LABEL_COLOR = "COULEUR DE L'AVATAR"
    override val PLAYER_CD_EDIT = "Modifier"

    // Home & Results
    override val HOME_CD_GAMES = "Jeux"
    override val HOME_CD_PLAYERS = "Joueurs"
    override val HOME_CD_FINGER_SELECTOR = "Jeu Sélecteur de doigts"
    override val HOME_TITLE_FINGER_SELECTOR = "Sélecteur de doigts"
    override val HOME_DESC_FINGER_SELECTOR = "Sélectionnez aléatoirement un joueur de départ avec plusieurs doigts"
    override val HOME_CD_COUNTER = "Jeu Compteur"
    override val HOME_CD_DICE = "Jeu Lanceur de dés"
    override val RESULTS_ACTION_BACK = "RETOUR À L'ACCUEIL"
    override val RESULTS_TITLE_TIE = "C'EST UNE ÉGALITÉ !"
    override val RESULTS_TITLE_WINNER = "NOUS AVONS UN GAGNANT !"
    override val RESULTS_SECTION_SCORES = "SCORES FINAUX"

    // Yahtzee - Statistics Screen
    override val YAHTZEE_STATS_GLOBAL = "⭐ Statistiques globales"
    override val YAHTZEE_STATS_SELECT_PLAYER = "Sélectionner un joueur"
    override val YAHTZEE_STATS_NO_DATA = "Aucune donnée disponible"

    // Player Statistics Sections
    override val YAHTZEE_STATS_OVERALL_PERFORMANCE = "📊 Performances globales"
    override val YAHTZEE_STATS_SCORE_BOX = "📈 Performances de la boîte de pointage"
    override val YAHTZEE_STATS_RECENT_GAMES = "🎮 Parties récentes"

    // Player Statistics Labels
    override val YAHTZEE_STATS_TOTAL_GAMES = "Nombre total de parties"
    override val YAHTZEE_STATS_FINISHED_GAMES = "Parties terminées"
    override val YAHTZEE_STATS_WINS = "Victoires"
    override val YAHTZEE_STATS_AVERAGE_SCORE = "Score moyen"
    override val YAHTZEE_STATS_PERSONAL_BEST = "Meilleur résultat personnel"
    override val YAHTZEE_STATS_TOTAL_YAHTZEES = "Total de Yahtzees"
    override val YAHTZEE_STATS_YAHTZEE_RATE = "Taux de Yahtzee"
    override val YAHTZEE_STATS_UPPER_SECTION = "Section supérieure"
    override val YAHTZEE_STATS_LOWER_SECTION = "Section inférieure"
    override val YAHTZEE_STATS_BONUS_RATE = "Taux de bonus"

    // Global Statistics Sections
    override val YAHTZEE_STATS_GLOBAL_OVERALL = "📊 Statistiques globales"
    override val YAHTZEE_STATS_GLOBAL_FUN_FACTS = "🎲 Faits amusants"
    override val YAHTZEE_STATS_GLOBAL_LEADERBOARDS = "🏆 Classements"
    override val YAHTZEE_STATS_GLOBAL_CATEGORY = "📈 Performances par catégorie"

    // Global Statistics Labels
    override val YAHTZEE_STATS_GLOBAL_FINISHED = "Parties terminées"
    override val YAHTZEE_STATS_GLOBAL_TOTAL_PLAYERS = "Nombre total de joueurs"
    override val YAHTZEE_STATS_GLOBAL_MOST_ACTIVE = "Joueur le plus actif"
    override val YAHTZEE_STATS_GLOBAL_HIGH_SCORE = "Meilleur score de tous les temps"
    override val YAHTZEE_STATS_GLOBAL_AVERAGE_SCORE = "Score moyen"
    override val YAHTZEE_STATS_GLOBAL_TOTAL_YAHTZEES = "Total de Yahtzees"
    override val YAHTZEE_STATS_GLOBAL_YAHTZEE_RATE = "Taux de Yahtzee"
    override val YAHTZEE_STATS_GLOBAL_MOST_YAHTZEES_GAME = "Plus de Yahtzees (partie unique)"
    override val YAHTZEE_STATS_GLOBAL_UPPER_BONUS = "Taux de bonus supérieur"
    override val YAHTZEE_STATS_GLOBAL_DICE_ROLLS = "Lancers de dés estimés"
    override val YAHTZEE_STATS_GLOBAL_POINTS_SCORED = "Points total marqués"
    override val YAHTZEE_STATS_GLOBAL_AVG_PLAYERS = "Moy. joueurs/partie"
    override val YAHTZEE_STATS_GLOBAL_LUCKIEST = "Joueur le plus chanceux"
    override val YAHTZEE_STATS_GLOBAL_MOST_CONSISTENT = "Plus constant"
    override val YAHTZEE_STATS_GLOBAL_MOST_SCORED = "Catégorie la plus marquée"
    override val YAHTZEE_STATS_GLOBAL_LEAST_SCORED = "Catégorie la moins marquée"
    override val YAHTZEE_STATS_GLOBAL_BEST_AVG = "Meilleure catégorie moyenne"

    // Leaderboards
    override val YAHTZEE_STATS_LEADERBOARD_MOST_WINS = "Plus de victoires"
    override val YAHTZEE_STATS_LEADERBOARD_HIGHEST_SCORES = "Meilleurs scores"
    override val YAHTZEE_STATS_LEADERBOARD_MOST_YAHTZEES = "Plus de Yahtzees"

    // Category Heatmap Sections
    override val YAHTZEE_STATS_HEATMAP_UPPER = "SECTION SUPÉRIEURE"
    override val YAHTZEE_STATS_HEATMAP_LOWER = "SECTION INFÉRIEURE"

    // Format Strings
    override val YAHTZEE_FORMAT_WINS = "%s (%s)"
    override val YAHTZEE_FORMAT_YAHTZEE_RATE = "%s par partie"
    override val YAHTZEE_FORMAT_ACTIVE_PLAYER = "%s (%d parties)"
    override val YAHTZEE_FORMAT_HIGH_SCORE = "%d par %s"
    override val YAHTZEE_FORMAT_MOST_YAHTZEES = "%d par %s"
    override val YAHTZEE_FORMAT_PLAYER_COUNT = "%d joueurs"
    override val YAHTZEE_FORMAT_LUCKIEST_PLAYER = "%s (%s/partie)"
    override val YAHTZEE_FORMAT_CATEGORY_AVG = "%s (%s)"
    override val YAHTZEE_FORMAT_WINNER_SCORE = "Gagnant : %s"

    // Rank Indicators
    override val YAHTZEE_RANK_FIRST = "🥇"
    override val YAHTZEE_RANK_SECOND = "🥈"
    override val YAHTZEE_RANK_THIRD = "🥉"
    override val YAHTZEE_RANK_FORMAT = "#%d"
    override val YAHTZEE_RANK_FIRST_PLACE = "🏆 1er"
    override val YAHTZEE_RANK_SECOND_PLACE = "🥈 2e"
    override val YAHTZEE_RANK_THIRD_PLACE = "🥉 3e"

    // Error Messages
    override val YAHTZEE_ERROR_LOAD_FAILED = "Impossible de charger : %s"
    override val YAHTZEE_ERROR_STATS_FAILED = "Impossible de charger les statistiques : %s"
    override val YAHTZEE_ERROR_GLOBAL_FAILED = "Impossible de charger les statistiques globales : %s"

    // Player Management - Dialog Titles
    override val PLAYER_DIALOG_NEW_TITLE = "Nouveau joueur"
    override val PLAYER_DIALOG_EDIT_TITLE = "Modifier le joueur"

    // Player Selector Component
    override val PLAYER_SELECTOR_PLACEHOLDER = "Sélectionner %s"
    override val PLAYER_SELECTOR_CHANGE = "MODIFIER"
    override val PLAYER_SELECTOR_SELECT = "SÉLECTIONNER"
    override val PLAYER_SELECTOR_DIALOG_TITLE = "Choisir un joueur"
    override val PLAYER_SELECTOR_SEARCH_PLACEHOLDER = "Rechercher ou ajouter..."
    override val PLAYER_REACTIVATE_FORMAT = "Réactiver \"%s\""
    override val PLAYER_CREATE_FORMAT = "Créer \"%s\""

    // Dice Roller Screen
    override val DICE_SCREEN_TITLE = "Dés"

    // Color Picker
    override val COLOR_PICKER_CD = "Couleur personnalisée"
    override val COLOR_PICKER_DIALOG_TITLE = "Choisir une couleur"

    // Finger Selector
    override val FINGER_SELECTOR_LABEL_FINGERS = "Nombre de doigts"
    override val FINGER_SELECTOR_LABEL_GROUPS = "Nombre de groupes"

    // Counter Screen - Player Count Format
    override val COUNTER_FORMAT_PLAYER_COUNT = "%dJ"

    // Error Handling
    override val ERROR_UNKNOWN = "Inconnu"

    // Validation Messages
    override val ERROR_MIN_FINGERS = "Besoin d'au moins %d doigts"
    override val ERROR_PLAYER_COUNT_RANGE = "Le nombre de joueurs doit être entre %d et %d"
    override val PLAYERS_COUNT_FORMAT = "Joueurs (%d/%d)"
    override val PLAYER_COUNT_DISPLAY = "%d joueur%s"

    // Settings Screen
    override val SETTINGS_LANGUAGE = "Langue"

    // Helper methods
    override fun playerCount(count: Int): String =
        "$count joueur${if (count > 1) "s" else ""}"
}

declare module 'ironquest' {
  export const enum ActionType {
    LAMP = 'LAMP',
    QUEST = 'QUEST',
    TRAIN = 'TRAIN',
  }

  export const enum Skill {
    AGILITY = 'AGILITY',
    ATTACK = 'ATTACK',
    CONSTITUTION = 'CONSTITUTION',
    CONSTRUCTION = 'CONSTRUCTION',
    COOKING = 'COOKING',
    CRAFTING = 'CRAFTING',
    DEFENCE = 'DEFENCE',
    DIVINATION = 'DIVINATION',
    DUNGEONEERING = 'DUNGEONEERING',
    FARMING = 'FARMING',
    FIREMAKING = 'FIREMAKING',
    FISHING = 'FISHING',
    FLETCHING = 'FLETCHING',
    HERBLORE = 'HERBLORE',
    HUNTER = 'HUNTER',
    INVENTION = 'INVENTION',
    MAGIC = 'MAGIC',
    MINING = 'MINING',
    PRAYER = 'PRAYER',
    RANGED = 'RANGED',
    RUNECRAFTING = 'RUNECRAFTING',
    SLAYER = 'SLAYER',
    SMITHING = 'SMITHING',
    STRENGTH = 'STRENGTH',
    SUMMONING = 'SUMMONING',
    THIEVING = 'THIEVING',
    WOODCUTTING = 'WOODCUTTING',
  }

  export interface Player {
    name: string;
    levels: Map<Skill, number>;
    questPoints: number;
    totalLevel: number;
    combatLevel: number;
  }

  export interface Action {
    type: ActionType;
    player: Player;
    future: boolean;
    message: string;
  }

  export interface Quest {
    displayName: string;
  }

  export interface LampAction extends Action {
    quest: Quest;
  }

  export interface QuestAction extends Action {
    quest: Quest;
  }

  export interface TrainAction extends Action {}

  export interface PathStats {
    percentComplete: number;
  }

  export interface Path {
    actions: Action[];
    stats: PathStats;
  }

  export const enum QuestAccessFilter {
    ALL = 'ALL',
    FREE = 'FREE',
    MEMBERS = 'MEMBERS',
  }

  export const enum QuestPriority {
    MAXIMUM = 'MAXIMUM',
    HIGH = 'HIGH',
    NORMAL = 'NORMAL',
    LOW = 'LOW',
    MINIMUM = 'MINIMUM',
  }

  export const enum QuestTypeFilter {
    ALL = 'ALL',
    QUESTS = 'QUESTS',
    SAGAS = 'SAGAS',
    MINIQUESTS = 'MINIQUESTS',
  }

  export interface PathFinderParameters {
    name?: string;
    accessFilter?: QuestAccessFilter;
    ironman?: boolean;
    recommended?: boolean;
    lampSkills?: Skill[];
    questPriorities?: Map<number, QuestPriority>;
    typeFilter?: QuestTypeFilter;
  }

  export interface PathFinderListener {
    start(): void;

    success(path: Path): void;

    failure(response: unknown): void;
  }

  export interface PathFinder {
    listeners: PathFinderListener[];
    parameters: PathFinderParameters;

    find(): void;
  }
}

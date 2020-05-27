export enum ActionType {
  LAMP = 'LAMP',
  QUEST = 'QUEST',
  TRAIN = 'TRAIN',
}

export enum Skill {
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

export type TrainAction = Action;

export interface PathStats {
  percentComplete: number;
}

export interface Path {
  actions: Action[];
  stats: PathStats;
}

export enum QuestAccessFilter {
  ALL = 'ALL',
  FREE = 'FREE',
  MEMBERS = 'MEMBERS',
}

export enum QuestPriority {
  MAXIMUM = 'MAXIMUM',
  HIGH = 'HIGH',
  NORMAL = 'NORMAL',
  LOW = 'LOW',
  MINIMUM = 'MINIMUM',
}

export enum QuestTypeFilter {
  ALL = 'ALL',
  QUESTS = 'QUESTS',
  SAGAS = 'SAGAS',
  MINIQUESTS = 'MINIQUESTS',
}

export enum PathFinderAlgorithm {
  DEFAULT = 'DEFAULT',
  SMART_PRIORITIES = 'SMART_PRIORITIES',
}

export interface QuestPriorities {
  [key: number]: QuestPriority;
}

export interface QuestsParameters {
  name?: string;
  accessFilter?: QuestAccessFilter;
  typeFilter?: QuestTypeFilter;
}

export interface PathFinderParameters extends QuestsParameters {
  ironman?: boolean;
  recommended?: boolean;
  lampSkills?: Skill[];
  questPriorities?: QuestPriorities;
  algorithm?: PathFinderAlgorithm;
}

export interface Quest {
  id: number;
  displayName: string;
  priority: QuestPriority;
}

export interface AjaxError {
  response: Response;
  parameters: QuestsParameters | PathFinderParameters;
}

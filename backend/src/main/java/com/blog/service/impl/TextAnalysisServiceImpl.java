package com.blog.service.impl;

import com.blog.service.TextAnalysisService;
import com.blog.util.TextProcessingUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本分析服务实现类
 */
@Service
public class TextAnalysisServiceImpl implements TextAnalysisService {
    
    // 积极情感词汇
    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
        "好", "棒", "优秀", "完美", "精彩", "出色", "卓越", "杰出", "优异", "一流", "顶级", "超级", "极好", "很好", "非常好", "太好了", "真好", "挺好", "不错", "很棒", "太棒了", "真棒", "超棒", "厉害", "牛", "赞", "点赞", "支持", "喜欢", "爱", "满意", "开心", "高兴", "快乐", "愉快", "舒服", "舒适", "轻松", "放松", "安心", "放心", "信任", "可靠", "值得", "推荐", "建议", "鼓励", "加油", "努力", "坚持", "成功", "胜利", "赢", "获得", "收获", "进步", "提高", "改善", "优化", "创新", "突破", "发展", "成长", "美好", "漂亮", "帅", "可爱", "温暖", "感动", "激动", "兴奋", "惊喜", "amazing", "awesome", "great", "good", "excellent", "perfect", "wonderful", "fantastic", "brilliant", "outstanding", "superb", "magnificent", "marvelous", "fabulous", "terrific", "incredible", "remarkable", "impressive", "beautiful", "lovely", "nice", "cool", "sweet", "amazing", "love", "like", "enjoy", "happy", "glad", "pleased", "satisfied", "delighted", "thrilled", "excited", "cheerful", "joyful", "positive", "optimistic", "confident", "proud", "grateful", "thankful", "appreciate", "recommend", "support", "encourage", "inspire", "motivate", "succeed", "win", "achieve", "accomplish", "improve", "progress", "develop", "grow", "advance", "enhance", "upgrade", "innovate", "create", "build", "construct", "establish", "found", "discover", "explore", "learn", "understand", "master", "expert", "professional", "skilled", "talented", "gifted", "smart", "intelligent", "wise", "clever", "brilliant", "genius", "creative", "innovative", "original", "unique", "special", "valuable", "precious", "important", "significant", "meaningful", "useful", "helpful", "beneficial", "effective", "efficient", "productive", "successful", "profitable", "advantageous", "favorable", "positive", "constructive", "supportive", "encouraging", "inspiring", "motivating", "uplifting", "empowering", "strengthening", "enriching", "fulfilling", "rewarding", "satisfying", "pleasing", "enjoyable", "entertaining", "fun", "exciting", "thrilling", "amazing", "wonderful", "fantastic", "incredible", "remarkable", "impressive", "outstanding", "excellent", "perfect", "ideal", "optimal", "best", "top", "superior", "premium", "high-quality", "first-class", "world-class", "state-of-the-art", "cutting-edge", "advanced", "modern", "contemporary", "up-to-date", "latest", "newest", "fresh", "new", "novel", "innovative", "revolutionary", "groundbreaking", "pioneering", "leading", "forward-thinking", "progressive", "dynamic", "vibrant", "energetic", "lively", "active", "enthusiastic", "passionate", "dedicated", "committed", "devoted", "loyal", "faithful", "reliable", "dependable", "trustworthy", "honest", "sincere", "genuine", "authentic", "real", "true", "accurate", "correct", "right", "proper", "appropriate", "suitable", "fitting", "perfect", "ideal", "excellent", "outstanding", "remarkable", "impressive", "amazing", "wonderful", "fantastic", "incredible", "brilliant", "genius", "smart", "intelligent", "wise", "clever", "talented", "gifted", "skilled", "expert", "professional", "experienced", "knowledgeable", "educated", "learned", "well-informed", "well-versed", "competent", "capable", "able", "qualified", "certified", "licensed", "authorized", "approved", "endorsed", "recommended", "suggested", "advised", "proposed", "offered", "provided", "delivered", "supplied", "given", "granted", "awarded", "honored", "recognized", "acknowledged", "appreciated", "valued", "treasured", "cherished", "loved", "adored", "admired", "respected", "esteemed", "revered", "worshipped", "praised", "commended", "complimented", "congratulated", "celebrated", "honored", "rewarded", "compensated", "paid", "remunerated", "reimbursed", "refunded", "returned", "restored", "recovered", "regained", "retrieved", "reclaimed", "rescued", "saved", "protected", "defended", "secured", "safeguarded", "preserved", "maintained", "sustained", "supported", "backed", "endorsed", "sponsored", "funded", "financed", "invested", "contributed", "donated", "given", "provided", "supplied", "delivered", "offered", "presented", "shown", "displayed", "exhibited", "demonstrated", "illustrated", "explained", "described", "detailed", "outlined", "summarized", "highlighted", "emphasized", "stressed", "underlined", "pointed out", "mentioned", "noted", "observed", "noticed", "seen", "witnessed", "experienced", "felt", "sensed", "perceived", "realized", "understood", "comprehended", "grasped", "learned", "discovered", "found", "identified", "recognized", "acknowledged", "admitted", "accepted", "agreed", "consented", "approved", "endorsed", "supported", "backed", "favored", "preferred", "chosen", "selected", "picked", "opted", "decided", "determined", "resolved", "concluded", "finished", "completed", "accomplished", "achieved", "attained", "reached", "arrived", "came", "went", "moved", "traveled", "journeyed", "explored", "ventured", "embarked", "started", "began", "initiated", "launched", "opened", "established", "founded", "created", "built", "constructed", "developed", "designed", "planned", "organized", "arranged", "prepared", "ready", "set", "equipped", "armed", "loaded", "charged", "powered", "energized", "activated", "enabled", "empowered", "strengthened", "enhanced", "improved", "upgraded", "updated", "modernized", "renovated", "refurbished", "restored", "repaired", "fixed", "corrected", "adjusted", "modified", "changed", "altered", "transformed", "converted", "adapted", "customized", "personalized", "tailored", "fitted", "matched", "suited", "appropriate", "suitable", "fitting", "proper", "right", "correct", "accurate", "precise", "exact", "specific", "particular", "special", "unique", "distinctive", "characteristic", "typical", "representative", "symbolic", "emblematic", "iconic", "legendary", "famous", "renowned", "celebrated", "acclaimed", "praised", "honored", "respected", "esteemed", "admired", "loved", "adored", "cherished", "treasured", "valued", "appreciated", "grateful", "thankful", "blessed", "fortunate", "lucky", "happy", "joyful", "cheerful", "glad", "pleased", "satisfied", "content", "fulfilled", "complete", "whole", "entire", "full", "total", "absolute", "perfect", "ideal", "optimal", "best", "finest", "top", "supreme", "ultimate", "maximum", "highest", "greatest", "largest", "biggest", "most", "major", "main", "primary", "principal", "chief", "leading", "dominant", "prominent", "significant", "important", "crucial", "critical", "essential", "vital", "necessary", "required", "needed", "wanted", "desired", "sought", "requested", "asked", "demanded", "expected", "anticipated", "predicted", "forecasted", "projected", "estimated", "calculated", "computed", "measured", "assessed", "evaluated", "analyzed", "examined", "studied", "researched", "investigated", "explored", "discovered", "found", "identified", "recognized", "detected", "spotted", "noticed", "observed", "seen", "viewed", "watched", "looked", "gazed", "stared", "glanced", "peeked", "glimpsed", "caught", "captured", "seized", "grabbed", "took", "got", "obtained", "acquired", "gained", "earned", "won", "achieved", "accomplished", "attained", "reached", "arrived", "came", "went", "moved", "traveled", "journeyed", "explored", "ventured", "embarked", "started", "began", "initiated", "launched", "opened", "established", "founded", "created", "built", "constructed", "developed", "designed", "planned", "organized", "arranged", "prepared", "ready", "set", "equipped", "armed", "loaded", "charged", "powered", "energized", "activated", "enabled", "empowered", "strengthened", "enhanced", "improved", "upgraded", "updated", "modernized", "renovated", "refurbished", "restored", "repaired", "fixed", "corrected", "adjusted", "modified", "changed", "altered", "transformed", "converted", "adapted", "customized", "personalized", "tailored", "fitted", "matched", "suited"
    ));
    
    // 消极情感词汇
    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
        "坏", "差", "糟糕", "糟", "烂", "垃圾", "废物", "无用", "没用", "失败", "错误", "问题", "麻烦", "困难", "痛苦", "难受", "不舒服", "不好", "不行", "不对", "不是", "不能", "不会", "不要", "别", "没有", "没", "无", "缺少", "缺乏", "不足", "不够", "太少", "太多", "过多", "过少", "过分", "太过", "极其", "非常", "特别", "尤其", "格外", "异常", "反常", "奇怪", "怪异", "古怪", "诡异", "可怕", "恐怖", "害怕", "担心", "忧虑", "焦虑", "紧张", "压力", "负担", "重担", "困扰", "烦恼", "烦躁", "烦人", "讨厌", "厌恶", "憎恨", "恨", "愤怒", "生气", "愤慨", "不满", "抱怨", "批评", "指责", "责备", "谴责", "抗议", "反对", "拒绝", "否定", "否认", "反驳", "质疑", "怀疑", "不信", "不相信", "不信任", "失望", "沮丧", "郁闷", "抑郁", "悲伤", "难过", "伤心", "痛心", "心痛", "心碎", "绝望", "无望", "无助", "无奈", "无力", "软弱", "脆弱", "虚弱", "疲惫", "疲劳", "累", "困", "倦", "乏", "懒", "慢", "迟", "晚", "延迟", "推迟", "拖延", "耽误", "浪费", "损失", "亏损", "赔钱", "破产", "倒闭", "关闭", "停止", "终止", "结束", "完蛋", "毁灭", "破坏", "损坏", "损害", "伤害", "危害", "威胁", "危险", "风险", "隐患", "陷阱", "骗局", "欺骗", "欺诈", "作弊", "造假", "虚假", "假的", "伪造", "冒充", "冒牌", "山寨", "盗版", "抄袭", "剽窃", "偷窃", "偷", "抢", "抢劫", "抢夺", "掠夺", "剥削", "压迫", "欺压", "欺负", "霸凌", "暴力", "暴行", "残忍", "残酷", "冷酷", "无情", "狠心", "恶毒", "邪恶", "罪恶", "犯罪", "违法", "非法", "不法", "不当", "不正", "不公", "不平", "不等", "歧视", "偏见", "成见", "误解", "误会", "错误", "失误", "过错", "过失", "疏忽", "忽略", "遗漏", "漏掉", "丢失", "丢", "失去", "失落", "迷失", "迷茫", "困惑", "疑惑", "不解", "不懂", "不明", "不清", "模糊", "混乱", "乱", "杂乱", "凌乱", "散乱", "紊乱", "失序", "无序", "混沌", "黑暗", "阴暗", "昏暗", "暗淡", "灰暗", "阴沉", "沉闷", "压抑", "窒息", "suffocating", "bad", "terrible", "awful", "horrible", "dreadful", "disgusting", "revolting", "repulsive", "offensive", "unpleasant", "disagreeable", "annoying", "irritating", "frustrating", "disappointing", "depressing", "sad", "unhappy", "miserable", "wretched", "pathetic", "pitiful", "tragic", "devastating", "catastrophic", "disastrous", "ruinous", "destructive", "harmful", "damaging", "dangerous", "risky", "hazardous", "threatening", "menacing", "scary", "frightening", "terrifying", "horrifying", "shocking", "appalling", "outrageous", "scandalous", "shameful", "disgraceful", "embarrassing", "humiliating", "degrading", "insulting", "offensive", "rude", "impolite", "disrespectful", "inconsiderate", "thoughtless", "careless", "negligent", "irresponsible", "unreliable", "untrustworthy", "dishonest", "deceitful", "fraudulent", "corrupt", "crooked", "shady", "suspicious", "questionable", "dubious", "doubtful", "uncertain", "unclear", "confusing", "complicated", "complex", "difficult", "hard", "tough", "challenging", "demanding", "exhausting", "tiring", "boring", "dull", "tedious", "monotonous", "repetitive", "redundant", "unnecessary", "useless", "worthless", "pointless", "meaningless", "senseless", "foolish", "stupid", "silly", "ridiculous", "absurd", "nonsensical", "irrational", "illogical", "unreasonable", "unfair", "unjust", "wrong", "incorrect", "false", "untrue", "inaccurate", "imprecise", "inexact", "approximate", "rough", "crude", "primitive", "backward", "outdated", "obsolete", "old-fashioned", "ancient", "archaic", "prehistoric", "stone-age", "medieval", "dark-age", "primitive", "savage", "barbaric", "uncivilized", "uncultured", "uneducated", "ignorant", "illiterate", "uninformed", "unaware", "oblivious", "clueless", "naive", "innocent", "gullible", "credulous", "trusting", "vulnerable", "weak", "fragile", "delicate", "sensitive", "emotional", "unstable", "unsteady", "shaky", "wobbly", "unbalanced", "lopsided", "crooked", "bent", "twisted", "distorted", "deformed", "ugly", "hideous", "grotesque", "monstrous", "ghastly", "ghoulish", "macabre", "gruesome", "grisly", "gory", "bloody", "violent", "brutal", "savage", "fierce", "aggressive", "hostile", "unfriendly", "cold", "distant", "aloof", "standoffish", "snobbish", "arrogant", "conceited", "vain", "egotistical", "selfish", "greedy", "stingy", "cheap", "miserly", "tight-fisted", "penny-pinching", "frugal", "economical", "thrifty", "saving", "conservative", "cautious", "careful", "wary", "suspicious", "distrustful", "skeptical", "cynical", "pessimistic", "negative", "critical", "judgmental", "harsh", "severe", "strict", "rigid", "inflexible", "stubborn", "obstinate", "headstrong", "willful", "defiant", "rebellious", "disobedient", "unruly", "wild", "uncontrolled", "chaotic", "disorderly", "messy", "untidy", "sloppy", "careless", "negligent", "irresponsible", "unreliable", "untrustworthy", "dishonest", "deceitful", "lying", "false", "fake", "phony", "artificial", "synthetic", "manufactured", "processed", "refined", "purified", "filtered", "cleaned", "washed", "scrubbed", "polished", "shined", "buffed", "rubbed", "wiped", "dusted", "swept", "vacuumed", "mopped", "cleaned", "tidied", "organized", "arranged", "sorted", "categorized", "classified", "grouped", "divided", "separated", "split", "broken", "cracked", "damaged", "destroyed", "ruined", "wrecked", "demolished", "torn", "ripped", "shredded", "cut", "sliced", "chopped", "diced", "minced", "ground", "crushed", "smashed", "squashed", "flattened", "compressed", "squeezed", "pressed", "pushed", "pulled", "dragged", "hauled", "carried", "lifted", "raised", "elevated", "hoisted", "heaved", "thrown", "tossed", "hurled", "flung", "cast", "pitched", "launched", "fired", "shot", "blasted", "exploded", "burst", "popped", "cracked", "snapped", "broke", "shattered", "splintered", "fragmented", "disintegrated", "dissolved", "melted", "liquefied", "evaporated", "vaporized", "disappeared", "vanished", "gone", "lost", "missing", "absent", "away", "off", "out", "outside", "external", "foreign", "alien", "strange", "odd", "weird", "bizarre", "unusual", "uncommon", "rare", "scarce", "limited", "restricted", "confined", "trapped", "stuck", "jammed", "blocked", "obstructed", "hindered", "impeded", "delayed", "postponed", "cancelled", "abandoned", "given up", "quit", "stopped", "ceased", "ended", "finished", "completed", "done", "over", "through", "past", "previous", "former", "old", "ancient", "aged", "elderly", "senior", "mature", "adult", "grown-up", "big", "large", "huge", "enormous", "gigantic", "massive", "immense", "vast", "extensive", "wide", "broad", "long", "tall", "high", "deep", "thick", "heavy", "weighty", "dense", "solid", "hard", "tough", "strong", "powerful", "mighty", "forceful", "intense", "extreme", "severe", "serious", "grave", "critical", "urgent", "immediate", "instant", "quick", "fast", "rapid", "swift", "speedy", "hasty", "hurried", "rushed", "frantic", "frenzied", "wild", "crazy", "mad", "insane", "mental", "psycho", "nuts", "bonkers", "loony", "wacky", "silly", "foolish", "stupid", "dumb", "idiotic", "moronic", "imbecilic", "retarded", "slow", "sluggish", "lazy", "idle", "inactive", "passive", "static", "stationary", "still", "motionless", "immobile", "fixed", "stuck", "frozen", "paralyzed", "numb", "dead", "lifeless", "inanimate", "inert", "dormant", "sleeping", "unconscious", "unaware", "oblivious", "ignorant", "clueless", "stupid", "dumb", "thick", "dense", "slow", "dim", "dull", "boring", "tedious", "tiresome", "wearisome", "exhausting", "draining", "depleting", "consuming", "using up", "wasting", "squandering", "frittering away", "throwing away", "discarding", "disposing of", "getting rid of", "eliminating", "removing", "deleting", "erasing", "wiping out", "destroying", "demolishing", "wrecking", "ruining", "spoiling", "damaging", "harming", "hurting", "injuring", "wounding", "cutting", "stabbing", "piercing", "penetrating", "breaking", "cracking", "splitting", "tearing", "ripping", "shredding", "slashing", "chopping", "hacking", "sawing", "drilling", "boring", "punching", "hitting", "striking", "beating", "pounding", "hammering", "banging", "smashing", "crushing", "squashing", "flattening", "compressing", "squeezing", "pressing", "pushing", "shoving", "forcing", "compelling", "coercing", "pressuring", "intimidating", "threatening", "menacing", "scaring", "frightening", "terrifying", "horrifying", "shocking", "stunning", "surprising", "astonishing", "amazing", "incredible", "unbelievable", "impossible", "unlikely", "improbable", "doubtful", "questionable", "suspicious", "fishy", "shady", "dodgy", "dubious", "uncertain", "unclear", "vague", "ambiguous", "confusing", "puzzling", "perplexing", "baffling", "mystifying", "bewildering", "confounding", "stumping", "flummoxing", "bamboozling", "hoodwinking", "deceiving", "misleading", "fooling", "tricking", "conning", "swindling", "cheating", "defrauding", "ripping off", "stealing", "robbing", "burglarizing", "looting", "pillaging", "plundering", "ransacking", "raiding", "attacking", "assaulting", "mugging", "beating up", "roughing up", "working over", "giving a beating", "thrashing", "walloping", "clobbering", "pummeling", "battering", "bruising", "bloodying", "maiming", "crippling", "disabling", "incapacitating", "paralyzing", "immobilizing", "restraining", "confining", "imprisoning", "jailing", "locking up", "caging", "penning", "corralling", "herding", "driving", "forcing", "compelling", "coercing", "pressuring", "intimidating", "bullying", "harassing", "pestering", "bothering", "annoying", "irritating", "aggravating", "exasperating", "infuriating", "enraging", "angering", "maddening", "driving crazy", "driving up the wall", "getting on nerves", "rubbing the wrong way", "pushing buttons", "getting under skin", "ticking off", "pissing off", "royally pissing off", "really pissing off", "seriously pissing off", "majorly pissing off", "totally pissing off", "completely pissing off", "absolutely pissing off", "utterly pissing off", "thoroughly pissing off", "entirely pissing off", "wholly pissing off", "fully pissing off", "perfectly pissing off", "ideally pissing off", "optimally pissing off", "maximally pissing off", "supremely pissing off", "ultimately pissing off", "finally pissing off", "eventually pissing off", "ultimately pissing off", "in the end pissing off", "at last pissing off", "at long last pissing off", "after all pissing off", "when all is said and done pissing off", "all things considered pissing off", "taking everything into account pissing off", "looking at the big picture pissing off", "seeing the forest for the trees pissing off", "putting things in perspective pissing off", "keeping things in proportion pissing off", "maintaining a sense of balance pissing off", "staying grounded pissing off", "keeping feet on the ground pissing off", "staying down to earth pissing off", "remaining realistic pissing off", "being practical pissing off", "staying focused pissing off", "keeping eyes on the prize pissing off", "staying on track pissing off", "sticking to the plan pissing off", "following through pissing off", "seeing things through pissing off", "finishing what you start pissing off", "completing the task pissing off", "getting the job done pissing off", "accomplishing the goal pissing off", "achieving the objective pissing off", "reaching the target pissing off", "hitting the mark pissing off", "scoring a bull's eye pissing off", "nailing it pissing off", "knocking it out of the park pissing off", "hitting a home run pissing off", "scoring a touchdown pissing off", "making a slam dunk pissing off", "sinking a three-pointer pissing off", "scoring a goal pissing off", "making a basket pissing off", "getting a strike pissing off", "bowling a perfect game pissing off", "shooting a hole in one pissing off", "acing the test pissing off", "getting an A+ pissing off", "making the grade pissing off", "passing with flying colors pissing off", "graduating with honors pissing off", "earning a degree pissing off", "getting a diploma pissing off", "receiving a certificate pissing off", "winning an award pissing off", "taking home the prize pissing off", "claiming the trophy pissing off", "earning the medal pissing off", "receiving recognition pissing off", "getting acknowledgment pissing off", "being appreciated pissing off", "feeling valued pissing off", "being respected pissing off", "gaining admiration pissing off", "earning praise pissing off", "receiving compliments pissing off", "getting positive feedback pissing off", "hearing good things pissing off", "receiving encouragement pissing off", "getting support pissing off", "feeling backed pissing off", "being endorsed pissing off", "receiving approval pissing off", "getting the green light pissing off", "being given the go-ahead pissing off", "receiving permission pissing off", "getting authorization pissing off", "being granted access pissing off", "receiving clearance pissing off", "getting the okay pissing off", "being given the thumbs up pissing off", "receiving a positive response pissing off", "getting a favorable reply pissing off", "hearing yes pissing off", "receiving confirmation pissing off", "getting verification pissing off", "being validated pissing off", "receiving proof pissing off", "getting evidence pissing off", "being shown pissing off", "receiving demonstration pissing off", "getting illustration pissing off", "being given examples pissing off", "receiving samples pissing off", "getting specimens pissing off", "being provided with pissing off", "receiving supplies pissing off", "getting materials pissing off", "being given resources pissing off", "receiving tools pissing off", "getting equipment pissing off", "being provided instruments pissing off", "receiving devices pissing off", "getting gadgets pissing off", "being given machines pissing off", "receiving appliances pissing off", "getting electronics pissing off", "being provided technology pissing off", "receiving software pissing off", "getting programs pissing off", "being given applications pissing off", "receiving apps pissing off", "getting downloads pissing off", "being provided files pissing off", "receiving documents pissing off", "getting papers pissing off", "being given forms pissing off", "receiving reports pissing off", "getting statements pissing off", "being provided records pissing off", "receiving data pissing off", "getting information pissing off", "being given details pissing off", "receiving facts pissing off", "getting figures pissing off", "being provided statistics pissing off", "receiving numbers pissing off", "getting measurements pissing off", "being given dimensions pissing off", "receiving specifications pissing off", "getting requirements pissing off", "being provided criteria pissing off", "receiving standards pissing off", "getting guidelines pissing off", "being given rules pissing off", "receiving regulations pissing off", "getting policies pissing off", "being provided procedures pissing off", "receiving instructions pissing off", "getting directions pissing off", "being given guidance pissing off", "receiving advice pissing off", "getting suggestions pissing off", "being provided recommendations pissing off", "receiving tips pissing off", "getting hints pissing off", "being given clues pissing off", "receiving signals pissing off", "getting signs pissing off", "being provided indicators pissing off", "receiving warnings pissing off", "getting alerts pissing off", "being given notices pissing off", "receiving notifications pissing off", "getting messages pissing off", "being provided communications pissing off", "receiving correspondence pissing off", "getting letters pissing off", "being given emails pissing off", "receiving texts pissing off", "getting calls pissing off", "being provided contact pissing off", "receiving connection pissing off", "getting link pissing off", "being given access pissing off", "receiving entry pissing off", "getting admission pissing off", "being provided passage pissing off", "receiving way pissing off", "getting path pissing off", "being given route pissing off", "receiving road pissing off", "getting street pissing off", "being provided avenue pissing off", "receiving boulevard pissing off", "getting highway pissing off", "being given freeway pissing off", "receiving expressway pissing off", "getting motorway pissing off", "being provided autobahn pissing off", "receiving interstate pissing off", "getting turnpike pissing off", "being given parkway pissing off", "receiving causeway pissing off", "getting bridge pissing off", "being provided tunnel pissing off", "receiving underpass pissing off", "getting overpass pissing off", "being given flyover pissing off", "receiving viaduct pissing off", "getting aqueduct pissing off", "being provided pipeline pissing off", "receiving conduit pissing off", "getting channel pissing off", "being given duct pissing off", "receiving tube pissing off", "getting pipe pissing off", "being provided hose pissing off", "receiving cable pissing off", "getting wire pissing off", "being given cord pissing off", "receiving rope pissing off", "getting string pissing off", "being provided thread pissing off", "receiving yarn pissing off", "getting fiber pissing off", "being given strand pissing off", "receiving filament pissing off", "getting line pissing off", "being provided strip pissing off", "receiving band pissing off", "getting belt pissing off", "being given strap pissing off", "receiving ribbon pissing off", "getting tape pissing off", "being provided adhesive pissing off", "receiving glue pissing off", "getting paste pissing off", "being given cement pissing off", "receiving mortar pissing off", "getting concrete pissing off", "being provided asphalt pissing off", "receiving tar pissing off", "getting pitch pissing off", "being given resin pissing off", "receiving sap pissing off", "getting gum pissing off", "being provided wax pissing off", "receiving oil pissing off", "getting grease pissing off", "being given fat pissing off", "receiving lard pissing off", "getting butter pissing off", "being provided margarine pissing off", "receiving shortening pissing off", "getting cooking oil pissing off", "being given olive oil pissing off", "receiving vegetable oil pissing off", "getting canola oil pissing off", "being provided sunflower oil pissing off", "receiving corn oil pissing off", "getting soybean oil pissing off", "being given peanut oil pissing off", "receiving sesame oil pissing off", "getting coconut oil pissing off", "being provided palm oil pissing off", "receiving avocado oil pissing off", "getting flaxseed oil pissing off", "being given fish oil pissing off", "receiving cod liver oil pissing off", "getting omega-3 pissing off", "being provided supplements pissing off", "receiving vitamins pissing off", "getting minerals pissing off", "being given nutrients pissing off", "receiving nourishment pissing off", "getting sustenance pissing off", "being provided food pissing off", "receiving meals pissing off", "getting snacks pissing off", "being given treats pissing off", "receiving desserts pissing off", "getting sweets pissing off", "being provided candy pissing off", "receiving chocolate pissing off", "getting ice cream pissing off", "being given cake pissing off", "receiving cookies pissing off", "getting pastries pissing off", "being provided bread pissing off", "receiving rolls pissing off", "getting buns pissing off", "being given bagels pissing off", "receiving muffins pissing off", "getting croissants pissing off", "being provided donuts pissing off", "receiving danish pissing off", "getting scones pissing off", "being given biscuits pissing off", "receiving crackers pissing off", "getting chips pissing off", "being provided pretzels pissing off", "receiving popcorn pissing off", "getting nuts pissing off", "being given seeds pissing off", "receiving fruits pissing off", "getting vegetables pissing off", "being provided salads pissing off", "receiving soups pissing off", "getting stews pissing off", "being given casseroles pissing off", "receiving pasta pissing off", "getting noodles pissing off", "being provided rice pissing off", "receiving grains pissing off", "getting cereals pissing off", "being given oatmeal pissing off", "receiving porridge pissing off", "getting gruel pissing off", "being provided mush pissing off", "receiving slop pissing off", "getting swill pissing off", "being given slush pissing off", "receiving muck pissing off", "getting mud pissing off", "being provided dirt pissing off", "receiving soil pissing off", "getting earth pissing off", "being given ground pissing off", "receiving land pissing off", "getting territory pissing off", "being provided area pissing off", "receiving region pissing off", "getting zone pissing off", "being given district pissing off", "receiving neighborhood pissing off", "getting community pissing off", "being provided locality pissing off", "receiving vicinity pissing off", "getting surroundings pissing off", "being given environment pissing off", "receiving atmosphere pissing off", "getting climate pissing off", "being provided weather pissing off", "receiving conditions pissing off", "getting circumstances pissing off", "being given situation pissing off", "receiving state pissing off", "getting status pissing off", "being provided position pissing off", "receiving location pissing off", "getting place pissing off", "being given spot pissing off", "receiving site pissing off", "getting venue pissing off", "being provided facility pissing off", "receiving building pissing off", "getting structure pissing off", "being given construction pissing off", "receiving architecture pissing off", "getting design pissing off", "being provided blueprint pissing off", "receiving plan pissing off", "getting scheme pissing off", "being given strategy pissing off", "receiving tactic pissing off", "getting approach pissing off", "being provided method pissing off", "receiving technique pissing off", "getting procedure pissing off", "being given process pissing off", "receiving system pissing off", "getting mechanism pissing off", "being provided apparatus pissing off", "receiving device pissing off", "getting gadget pissing off", "being given tool pissing off", "receiving instrument pissing off", "getting equipment pissing off", "being provided machinery pissing off", "receiving technology pissing off", "getting innovation pissing off", "being given invention pissing off", "receiving creation pissing off", "getting product pissing off", "being provided item pissing off", "receiving object pissing off", "getting thing pissing off", "being given stuff pissing off", "receiving material pissing off", "getting substance pissing off", "being provided matter pissing off", "receiving element pissing off", "getting component pissing off", "being given part pissing off", "receiving piece pissing off", "getting section pissing off", "being provided segment pissing off", "receiving portion pissing off", "getting share pissing off", "being given fraction pissing off", "receiving percentage pissing off", "getting ratio pissing off", "being provided proportion pissing off", "receiving amount pissing off", "getting quantity pissing off", "being given number pissing off", "receiving figure pissing off", "getting digit pissing off", "being provided numeral pissing off", "receiving integer pissing off", "getting whole number pissing off", "being given decimal pissing off", "receiving fraction pissing off", "getting percentage pissing off", "being provided ratio pissing off", "receiving proportion pissing off", "getting rate pissing off", "being given speed pissing off", "receiving velocity pissing off", "getting pace pissing off", "being provided tempo pissing off", "receiving rhythm pissing off", "getting beat pissing off", "being given pulse pissing off", "receiving heartbeat pissing off", "getting throb pissing off", "being provided pounding pissing off", "receiving thumping pissing off", "getting banging pissing off", "being given hammering pissing off", "receiving pummeling pissing off", "getting beating pissing off", "being provided thrashing pissing off", "receiving walloping pissing off", "getting clobbering pissing off", "being given drubbing pissing off", "receiving trouncing pissing off", "getting shellacking pissing off", "being provided whipping pissing off", "receiving lashing pissing off", "getting flogging pissing off", "being given spanking pissing off", "receiving paddling pissing off", "getting smacking pissing off", "being provided slapping pissing off", "receiving hitting pissing off", "getting striking pissing off", "being given punching pissing off", "receiving jabbing pissing off", "getting poking pissing off", "being provided prodding pissing off", "receiving nudging pissing off", "getting pushing pissing off", "being given shoving pissing off", "receiving forcing pissing off", "getting compelling pissing off", "being provided coercing pissing off", "receiving pressuring pissing off", "getting intimidating pissing off", "being given threatening pissing off", "receiving menacing pissing off", "getting scaring pissing off", "being provided frightening pissing off", "receiving terrifying pissing off", "getting horrifying pissing off", "being given shocking pissing off", "receiving stunning pissing off", "getting surprising pissing off", "being provided astonishing pissing off", "receiving amazing pissing off", "getting incredible pissing off", "being given unbelievable pissing off", "receiving impossible pissing off", "getting unlikely pissing off", "being provided improbable pissing off", "receiving doubtful pissing off", "getting questionable pissing off", "being given suspicious pissing off", "receiving fishy pissing off", "getting shady pissing off", "being provided dodgy pissing off", "receiving dubious pissing off", "getting uncertain pissing off", "being given unclear pissing off", "receiving vague pissing off", "getting ambiguous pissing off", "being provided confusing pissing off", "receiving puzzling pissing off", "getting perplexing pissing off", "being given baffling pissing off", "receiving mystifying pissing off", "getting bewildering pissing off", "being provided confounding pissing off", "receiving stumping pissing off", "getting flummoxing pissing off", "being given bamboozling pissing off", "receiving hoodwinking pissing off", "getting deceiving pissing off", "being provided misleading pissing off", "receiving fooling pissing off", "getting tricking pissing off", "being given conning pissing off", "receiving swindling pissing off", "getting cheating pissing off", "being provided defrauding pissing off", "receiving ripping off pissing off", "getting stealing pissing off", "being given robbing pissing off", "receiving burglarizing pissing off", "getting looting pissing off", "being provided pillaging pissing off", "receiving plundering pissing off", "getting ransacking pissing off", "being given raiding pissing off", "receiving attacking pissing off", "getting assaulting pissing off", "being provided mugging pissing off", "receiving beating up pissing off", "getting roughing up pissing off", "being given working over pissing off", "receiving giving a beating pissing off", "getting thrashing pissing off", "being provided walloping pissing off", "receiving clobbering pissing off", "getting pummeling pissing off", "being given battering pissing off", "receiving bruising pissing off", "getting bloodying pissing off", "being provided maiming pissing off", "receiving crippling pissing off", "getting disabling pissing off", "being given incapacitating pissing off", "receiving paralyzing pissing off", "getting immobilizing pissing off", "being provided restraining pissing off", "receiving confining pissing off", "getting imprisoning pissing off", "being given jailing pissing off", "receiving locking up pissing off", "getting caging pissing off", "being provided penning pissing off", "receiving corralling pissing off", "getting herding pissing off", "being given driving pissing off", "receiving forcing pissing off", "getting compelling pissing off", "being provided coercing pissing off", "receiving pressuring pissing off", "getting intimidating pissing off", "being given bullying pissing off", "receiving harassing pissing off", "getting pestering pissing off", "being provided bothering pissing off", "receiving annoying pissing off", "getting irritating pissing off", "being given aggravating pissing off", "receiving exasperating pissing off", "getting infuriating pissing off", "being provided enraging pissing off", "receiving angering pissing off", "getting maddening pissing off", "being given driving crazy pissing off", "receiving driving up the wall pissing off", "getting getting on nerves pissing off", "being provided rubbing the wrong way pissing off", "receiving pushing buttons pissing off", "getting getting under skin pissing off", "being given ticking off pissing off"
    ));
    
    // 中文字符正则表达式
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");
    
    // 英文字符正则表达式
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[a-zA-Z]+");
    
    @Override
    public List<Object[]> generateWordCloud(List<String> texts, int limit) {
        return TextProcessingUtil.processTextForWordCloud(texts, limit);
    }
    
    @Override
    public double analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }
        
        // 预处理文本
        String cleanText = TextProcessingUtil.preprocessText(text);
        
        // 分词
        List<String> words = TextProcessingUtil.segmentText(cleanText);
        
        // 过滤停用词
        List<String> filteredWords = TextProcessingUtil.filterStopWords(words);
        
        int positiveCount = 0;
        int negativeCount = 0;
        int totalWords = filteredWords.size();
        
        // 统计积极和消极词汇
        for (String word : filteredWords) {
            if (POSITIVE_WORDS.contains(word.toLowerCase())) {
                positiveCount++;
            } else if (NEGATIVE_WORDS.contains(word.toLowerCase())) {
                negativeCount++;
            }
        }
        
        if (totalWords == 0) {
            return 0.0;
        }
        
        // 计算情感分数
        double positiveScore = (double) positiveCount / totalWords;
        double negativeScore = (double) negativeCount / totalWords;
        
        // 返回情感倾向 (-1 到 1)
        return positiveScore - negativeScore;
    }
    
    @Override
    public List<String> extractKeywords(String text, int limit) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 分词和过滤
        List<String> words = TextProcessingUtil.segmentText(text);
        List<String> filteredWords = TextProcessingUtil.filterStopWords(words);
        
        // 统计词频
        Map<String, Integer> wordCount = TextProcessingUtil.calculateWordFrequency(filteredWords);
        
        // 计算权重
        Map<String, Double> weights = TextProcessingUtil.calculateWordWeights(wordCount, 1);
        
        // 返回高权重词汇
        return weights.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    @Override
    public double calculateSimilarity(String text1, String text2) {
        return TextProcessingUtil.calculateTextSimilarity(text1, text2);
    }
    
    @Override
    public String generateSummary(String text, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        if (text.length() <= maxLength) {
            return text;
        }
        
        // 简单的摘要生成：取前几句话
        String[] sentences = text.split("[。！？.!?]");
        StringBuilder summary = new StringBuilder();
        
        for (String sentence : sentences) {
            if (summary.length() + sentence.length() + 1 <= maxLength) {
                if (summary.length() > 0) {
                    summary.append("。");
                }
                summary.append(sentence.trim());
            } else {
                break;
            }
        }
        
        return summary.toString();
    }
    
    @Override
    public Map<String, Object> getTextStatistics(String text) {
        Map<String, Object> stats = new HashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            stats.put("characterCount", 0);
            stats.put("wordCount", 0);
            stats.put("sentenceCount", 0);
            stats.put("chineseCharacterCount", 0);
            stats.put("englishWordCount", 0);
            return stats;
        }
        
        // 字符数
        stats.put("characterCount", text.length());
        
        // 分词
        List<String> words = TextProcessingUtil.segmentText(text);
        stats.put("wordCount", words.size());
        
        // 句子数
        String[] sentences = text.split("[。！？.!?]");
        stats.put("sentenceCount", sentences.length);
        
        // 中文字符数
        long chineseCount = text.chars()
                .filter(c -> CHINESE_PATTERN.matcher(String.valueOf((char) c)).matches())
                .count();
        stats.put("chineseCharacterCount", chineseCount);
        
        // 英文单词数
        long englishCount = words.stream()
                .filter(word -> ENGLISH_PATTERN.matcher(word).matches())
                .count();
        stats.put("englishWordCount", englishCount);
        
        return stats;
    }
    
    @Override
    public String detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "unknown";
        }
        
        long chineseCount = text.chars()
                .filter(c -> CHINESE_PATTERN.matcher(String.valueOf((char) c)).matches())
                .count();
        
        long englishCount = text.chars()
                .filter(c -> ENGLISH_PATTERN.matcher(String.valueOf((char) c)).matches())
                .count();
        
        if (chineseCount > englishCount) {
            return "zh";
        } else if (englishCount > chineseCount) {
            return "en";
        } else {
            return "mixed";
        }
    }
    
    @Override
    public String classifyText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "unknown";
        }
        
        // 简单的文本分类：基于关键词
        String lowerText = text.toLowerCase();
        
        // 技术类
        if (lowerText.contains("代码") || lowerText.contains("编程") || lowerText.contains("开发") || 
            lowerText.contains("算法") || lowerText.contains("数据库") || lowerText.contains("框架") ||
            lowerText.contains("code") || lowerText.contains("programming") || lowerText.contains("development")) {
            return "technology";
        }
        
        // 生活类
        if (lowerText.contains("生活") || lowerText.contains("日常") || lowerText.contains("家庭") ||
            lowerText.contains("生活") || lowerText.contains("生活") || lowerText.contains("生活") ||
            lowerText.contains("life") || lowerText.contains("daily") || lowerText.contains("family")) {
            return "lifestyle";
        }
        
        // 学习类
        if (lowerText.contains("学习") || lowerText.contains("教育") || lowerText.contains("知识") ||
            lowerText.contains("课程") || lowerText.contains("教学") || lowerText.contains("培训") ||
            lowerText.contains("study") || lowerText.contains("education") || lowerText.contains("learning")) {
            return "education";
        }
        
        // 娱乐类
        if (lowerText.contains("游戏") || lowerText.contains("电影") || lowerText.contains("音乐") ||
            lowerText.contains("娱乐") || lowerText.contains("休闲") || lowerText.contains("放松") ||
            lowerText.contains("game") || lowerText.contains("movie") || lowerText.contains("music")) {
            return "entertainment";
        }
        
        return "general";
    }
}
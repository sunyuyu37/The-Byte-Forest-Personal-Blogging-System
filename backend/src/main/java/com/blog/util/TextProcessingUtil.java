package com.blog.util;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本处理工具类
 * 用于评论文本的预处理、分词、词频统计等操作
 */
public class TextProcessingUtil {
    
    // 中文停用词列表（优化版本，增加更多语气词和无意义词汇）
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        // 基础停用词
        "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这", "那", "他", "她", "它", "们", "这个", "那个", "什么", "怎么", "为什么", "因为", "所以", "但是", "然后", "还是", "或者", "如果", "虽然", "可以", "应该", "能够", "已经", "正在", "将要", "可能", "一定", "非常", "特别", "比较", "更加", "最", "太", "很多", "一些", "几个", "每个", "所有", "任何", "没", "别", "不要", "不会", "不是", "不能", "不用", "没关系", "没问题", "谢谢", "感谢", "请", "对不起", "不好意思",
        // 语气词和感叹词
        "哈哈", "呵呵", "嗯", "啊", "哦", "吧", "呢", "吗", "嘛", "哟", "咯", "喔", "额", "嗯嗯", "哈", "嘿", "嘻嘻", "哎", "唉", "哇", "咦", "咳", "嗯哼", "嗯啊", "哼", "嗯呢", "嗯哦", "嗯呀", "嗯嘛", "嗯吧", "嗯咯", "嗯喔", "嗯额", "嗯哈", "嗯嘿", "嗯嘻", "嗯哎", "嗯唉", "嗯哇", "嗯咦", "嗯咳",
        "哈哈哈", "呵呵呵", "嘻嘻嘻", "嘿嘿", "嘿嘿嘿", "嗯嗯嗯", "啊啊", "啊啊啊", "哦哦", "哦哦哦", "哇哇", "哇哇哇", "哎呀", "哎哟", "哎呀呀", "哎哟哟", "唉唉", "唉唉唉",
        // 网络用语和表情
        "233", "666", "哈哈哈哈", "嘻嘻嘻嘻", "呵呵呵呵", "嘿嘿嘿嘿", "emmm", "emmmm", "额额", "额额额", "呃呃", "呃呃呃", "诶", "诶诶", "诶诶诶", "咦咦", "咦咦咦", "咳咳", "咳咳咳",
        // 程度副词和修饰词
        "挺", "蛮", "相当", "十分", "极其", "超级", "超", "巨", "贼", "老", "死", "活", "生", "真", "假", "纯", "完全", "彻底", "绝对", "肯定", "确实", "的确", "果然", "竟然", "居然", "简直", "几乎", "差不多", "大概", "大约", "左右", "上下", "前后", "内外",
        // 时间词
        "今天", "昨天", "明天", "后天", "前天", "现在", "刚才", "刚刚", "马上", "立刻", "立即", "随时", "随便", "经常", "总是", "从来", "一直", "始终", "永远", "曾经", "以前", "以后", "之前", "之后", "当时", "那时", "这时", "同时", "平时", "有时", "偶尔", "忽然", "突然", "瞬间", "瞬时",
        // 地点词
        "这里", "那里", "哪里", "到处", "处处", "各处", "某处", "别处", "他处", "此处", "彼处", "远处", "近处", "高处", "低处", "深处", "浅处", "内部", "外部", "中间", "旁边", "附近", "周围", "四周", "左右", "上下", "前后", "东西", "南北",
        // 人称代词扩展
        "咱", "咱们", "大家", "各位", "诸位", "您", "您们", "人家", "别人", "他人", "某人", "谁", "谁们", "什么人", "哪个人", "这人", "那人", "此人", "彼人",
        // 疑问词
        "哪", "哪个", "哪些", "哪里", "哪儿", "何", "何时", "何地", "何人", "何事", "何故", "为何", "如何", "怎样", "怎么样", "多少", "几多", "多大", "多长", "多高", "多深", "多宽", "多重", "多远", "多久",
        // 连接词
        "而且", "并且", "以及", "还有", "另外", "此外", "除了", "除此之外", "不仅", "不但", "不光", "不只", "岂止", "何止", "更", "还", "又", "再", "也", "亦", "同样", "一样", "同时", "与此同时", "另一方面", "反之", "相反", "然而", "不过", "只是", "只不过", "无非", "不外乎",
        // 助词
        "地", "得", "着", "过", "来", "去", "起来", "下去", "上去", "进来", "出去", "回来", "过来", "过去", "起", "下", "上", "进", "出", "回", "开", "关", "掉", "住", "好", "完", "光", "净", "尽", "满", "遍", "透", "通", "穿", "破", "烂", "坏", "死", "活", "生", "熟", "老", "新", "旧", "古", "今", "中", "外", "内", "里", "表", "面", "底", "顶", "头", "尾", "前", "后", "左", "右", "东", "西", "南", "北", "中央", "中心", "边", "角", "侧", "旁",
        // 数量词
        "一点", "一些", "一下", "一会", "一会儿", "一阵", "一阵子", "一段", "一段时间", "一瞬间", "一刹那", "一瞬", "一眨眼", "一转眼", "一晃", "一闪", "一溜烟", "一股脑", "一古脑", "一口气", "一下子", "一次性", "一次", "一回", "一趟", "一遍", "一番", "一通", "一顿", "一场", "一局", "一轮", "一圈", "一周", "一圆", "一方", "一片", "一块", "一团", "一堆", "一群", "一批", "一帮", "一伙", "一班", "一队", "一组", "一套", "一系列", "一连串", "一大串", "一长串", "一小串",
        // 英文停用词
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "will", "with", "the", "this", "but", "they", "have", "had", "what", "said", "each", "which", "she", "do", "how", "their", "if", "up", "out", "many", "then", "them", "these", "so", "some", "her", "would", "make", "like", "into", "him", "time", "two", "more", "go", "no", "way", "could", "my", "than", "first", "been", "call", "who", "oil", "sit", "now", "find", "down", "day", "did", "get", "come", "made", "may", "part", "over", "new", "sound", "take", "only", "little", "work", "know", "place", "year", "live", "me", "back", "give", "most", "very", "after", "thing", "our", "just", "name", "good", "sentence", "man", "think", "say", "great", "where", "help", "through", "much", "before", "line", "right", "too", "mean", "old", "any", "same", "tell", "boy", "follow", "came", "want", "show", "also", "around", "form", "three", "small", "set", "put", "end", "why", "again", "turn", "here", "off", "went", "old", "number", "great", "tell", "men", "say", "small", "every", "found", "still", "between", "mane", "should", "home", "big", "give", "air", "line", "set", "own", "under", "read", "last", "never", "us", "left", "end", "along", "while", "might", "next", "sound", "below", "saw", "something", "thought", "both", "few", "those", "always", "looked", "show", "large", "often", "together", "asked", "house", "don't", "world", "going", "want", "school", "important", "until", "form", "food", "keep", "children", "feet", "land", "side", "without", "boy", "once", "animal", "life", "enough", "took", "sometimes", "four", "head", "above", "kind", "began", "almost", "live", "page", "got", "earth", "need", "far", "hand", "high", "year", "mother", "light", "country", "father", "let", "night", "picture", "being", "study", "second", "soon", "story", "since", "white", "ever", "paper", "hard", "near", "sentence", "better", "best", "across", "during", "today", "however", "sure", "knew", "it's", "try", "told", "young", "sun", "thing", "whole", "hear", "example", "heard", "several", "change", "answer", "room", "sea", "against", "top", "turned", "learn", "point", "city", "play", "toward", "five", "himself", "usually", "money", "seen", "didn't", "car", "morning", "I'm", "body", "upon", "family", "later", "turn", "move", "face", "door", "cut", "done", "group", "true", "leave", "red", "friend", "began", "idea", "fish", "mountain", "stop", "once", "base", "hear", "horse", "cut", "sure", "watch", "color", "wood", "main", "enough", "plain", "girl", "usual", "young", "ready", "above", "ever", "red", "list", "though", "feel", "talk", "bird", "soon", "body", "dog", "family", "direct", "pose", "leave", "song", "measure", "door", "product", "black", "short", "numeral", "class", "wind", "question", "happen", "complete", "ship", "area", "half", "rock", "order", "fire", "south", "problem", "piece", "told", "knew", "pass", "since", "top", "whole", "king", "space", "heard", "best", "hour", "better", "during", "hundred", "five", "remember", "step", "early", "hold", "west", "ground", "interest", "reach", "fast", "verb", "sing", "listen", "six", "table", "travel", "less", "morning", "ten", "simple", "several", "vowel", "toward", "war", "lay", "against", "pattern", "slow", "center", "love", "person", "money", "serve", "appear", "road", "map", "rain", "rule", "govern", "pull", "cold", "notice", "voice", "unit", "power", "town", "fine", "certain", "fly", "fall", "lead", "cry", "dark", "machine", "note", "wait", "plan", "figure", "star", "box", "noun", "field", "rest", "correct", "able", "pound", "done", "beauty", "drive", "stood", "contain", "front", "teach", "week", "final", "gave", "green", "oh", "quick", "develop", "ocean", "warm", "free", "minute", "strong", "special", "mind", "behind", "clear", "tail", "produce", "fact", "street", "inch", "multiply", "nothing", "course", "stay", "wheel", "full", "force", "blue", "object", "decide", "surface", "deep", "moon", "island", "foot", "system", "busy", "test", "record", "boat", "common", "gold", "possible", "plane", "stead", "dry", "wonder", "laugh", "thousands", "ago", "ran", "check", "game", "shape", "equate", "hot", "miss", "brought", "heat", "snow", "tire", "bring", "yes", "distant", "fill", "east", "paint", "language", "among", "grand", "ball", "yet", "wave", "drop", "heart", "am", "present", "heavy", "dance", "engine", "position", "arm", "wide", "sail", "material", "size", "vary", "settle", "speak", "weight", "general", "ice", "matter", "circle", "pair", "include", "divide", "syllable", "felt", "perhaps", "pick", "sudden", "count", "square", "reason", "length", "represent", "art", "subject", "region", "energy", "hunt", "probable", "bed", "brother", "egg", "ride", "cell", "believe", "fraction", "forest", "sit", "race", "window", "store", "summer", "train", "sleep", "prove", "lone", "leg", "exercise", "wall", "catch", "mount", "wish", "sky", "board", "joy", "winter", "sat", "written", "wild", "instrument", "kept", "glass", "grass", "cow", "job", "edge", "sign", "visit", "past", "soft", "fun", "bright", "gas", "weather", "month", "million", "bear", "finish", "happy", "hope", "flower", "clothe", "strange", "gone", "jump", "baby", "eight", "village", "meet", "root", "buy", "raise", "solve", "metal", "whether", "push", "seven", "paragraph", "third", "shall", "held", "hair", "describe", "cook", "floor", "either", "result", "burn", "hill", "safe", "cat", "century", "consider", "type", "law", "bit", "coast", "copy", "phrase", "silent", "tall", "sand", "soil", "roll", "temperature", "finger", "industry", "value", "fight", "lie", "beat", "excite", "natural", "view", "sense", "ear", "else", "quite", "broke", "case", "middle", "kill", "son", "lake", "moment", "scale", "loud", "spring", "observe", "child", "straight", "consonant", "nation", "dictionary", "milk", "speed", "method", "organ", "pay", "age", "section", "dress", "cloud", "surprise", "quiet", "stone", "tiny", "climb", "bad", "oil", "blood", "touch", "grew", "cent", "mix", "team", "wire", "cost", "lost", "brown", "wear", "garden", "equal", "sent", "choose", "fell", "fit", "flow", "fair", "bank", "collect", "save", "control", "decimal", "gentle", "woman", "captain", "practice", "separate", "difficult", "doctor", "please", "protect", "noon", "whose", "locate", "ring", "character", "insect", "caught", "period", "indicate", "radio", "spoke", "atom", "human", "history", "effect", "electric", "expect", "crop", "modern", "element", "hit", "student", "corner", "party", "supply", "bone", "rail", "imagine", "provide", "agree", "thus", "capital", "won't", "chair", "danger", "fruit", "rich", "thick", "soldier", "process", "operate", "guess", "necessary", "sharp", "wing", "create", "neighbor", "wash", "bat", "rather", "crowd", "corn", "compare", "poem", "string", "bell", "depend", "meat", "rub", "tube", "famous", "dollar", "stream", "fear", "sight", "thin", "triangle", "planet", "hurry", "chief", "colony", "clock", "mine", "tie", "enter", "major", "fresh", "search", "send", "yellow", "gun", "allow", "print", "dead", "spot", "desert", "suit", "current", "lift", "rose", "continue", "block", "chart", "hat", "sell", "success", "company", "subtract", "event", "particular", "deal", "swim", "term", "opposite", "wife", "shoe", "shoulder", "spread", "arrange", "camp", "invent", "cotton", "born", "determine", "quart", "nine", "truck", "noise", "level", "chance", "gather", "shop", "stretch", "throw", "shine", "property", "column", "molecule", "select", "wrong", "gray", "repeat", "require", "broad", "prepare", "salt", "nose", "plural", "anger", "claim", "continent", "oxygen", "sugar", "death", "pretty", "skill", "women", "season", "solution", "magnet", "silver", "thank", "branch", "match", "suffix", "especially", "fig", "afraid", "huge", "sister", "steel", "discuss", "forward", "similar", "guide", "experience", "score", "apple", "bought", "led", "pitch", "coat", "mass", "card", "band", "rope", "slip", "win", "dream", "evening", "condition", "feed", "tool", "total", "basic", "smell", "valley", "nor", "double", "seat", "arrive", "master", "track", "parent", "shore", "division", "sheet", "substance", "favor", "connect", "post", "spend", "chord", "fat", "glad", "original", "share", "station", "dad", "bread", "charge", "proper", "bar", "offer", "segment", "slave", "duck", "instant", "market", "degree", "populate", "chick", "dear", "enemy", "reply", "drink", "occur", "support", "speech", "nature", "range", "steam", "motion", "path", "liquid", "log", "meant", "quotient", "teeth", "shell", "neck"
    ));
    
    // 标点符号和特殊字符的正则表达式
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[\\p{Punct}\\p{Space}\\p{Cntrl}]+");
    
    // 数字的正则表达式
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    
    // 英文单词的正则表达式
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z]+$");
    
    // 中文字符的正则表达式
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");
    
    // 中文词汇的正则表达式（2-6字）
    private static final Pattern CHINESE_WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]{2,6}");
    
    // 常见中文词组和专业术语词典
    private static final Set<String> COMMON_PHRASES = new HashSet<>(Arrays.asList(
        // 技术相关词汇
        "人工智能", "机器学习", "深度学习", "神经网络", "大数据", "云计算", "区块链", "物联网", "虚拟现实", "增强现实",
        "数据库", "算法", "编程", "软件开发", "前端开发", "后端开发", "全栈开发", "移动开发", "网站建设", "系统架构",
        "用户体验", "用户界面", "产品设计", "交互设计", "视觉设计", "响应式设计", "敏捷开发", "项目管理", "版本控制", "代码审查",
        "数据分析", "数据挖掘", "商业智能", "数据可视化", "统计分析", "预测模型", "机器视觉", "自然语言处理", "语音识别", "图像识别",
        "网络安全", "信息安全", "数据安全", "隐私保护", "加密技术", "防火墙", "入侵检测", "漏洞扫描", "安全审计", "风险评估",
        
        // 生活相关词汇
        "生活方式", "健康生活", "工作生活平衡", "时间管理", "效率提升", "个人成长", "职业发展", "学习方法", "阅读习惯", "运动健身",
        "饮食健康", "营养搭配", "心理健康", "压力管理", "情绪调节", "人际关系", "沟通技巧", "团队合作", "领导力", "创新思维",
        "理财规划", "投资理财", "消费观念", "购物指南", "旅游攻略", "美食推荐", "文化体验", "艺术欣赏", "音乐品味", "电影评论",
        
        // 学习相关词汇
        "在线学习", "远程教育", "终身学习", "知识管理", "学习计划", "复习方法", "记忆技巧", "思维导图", "笔记整理", "考试技巧",
        "语言学习", "英语学习", "口语练习", "听力训练", "阅读理解", "写作技巧", "翻译技能", "跨文化交流", "国际视野", "全球化思维",
        "专业技能", "职业技能", "软技能", "硬技能", "技能提升", "能力建设", "素质教育", "创新能力", "批判思维", "问题解决",
        
        // 娱乐相关词汇
        "游戏体验", "电子竞技", "手机游戏", "网络游戏", "单机游戏", "游戏设计", "游戏开发", "游戏评测", "游戏攻略", "游戏社区",
        "影视作品", "电影推荐", "电视剧", "纪录片", "动画片", "综艺节目", "娱乐新闻", "明星八卦", "粉丝文化", "流行文化",
        "音乐欣赏", "音乐推荐", "歌曲分享", "音乐节", "演唱会", "音乐制作", "乐器学习", "音乐理论", "音乐风格", "音乐历史",
        
        // 商业相关词汇
        "商业模式", "创业精神", "企业管理", "市场营销", "品牌建设", "客户服务", "销售技巧", "商务谈判", "合作伙伴", "供应链管理",
        "电子商务", "网络营销", "社交媒体", "内容营销", "搜索引擎优化", "用户增长", "数据驱动", "精准营销", "转化率优化", "客户关系管理",
        "金融科技", "移动支付", "数字货币", "在线银行", "投资平台", "风险控制", "信用评估", "保险服务", "财富管理", "退休规划"
    ));
    
    /**
     * 预处理文本：清理HTML标签、特殊字符等
     */
    public static String preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // 移除HTML标签
        text = text.replaceAll("<[^>]+>", "");
        
        // 移除URL链接
        text = text.replaceAll("https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+", "");
        
        // 移除邮箱地址
        text = text.replaceAll("[\\w\\-._]+@[\\w\\-._]+\\.[A-Za-z]{2,}", "");
        
        // 移除多余的空白字符
        text = text.replaceAll("\\s+", " ").trim();
        
        return text;
    }
    
    /**
     * 中文分词（改进版本，增加智能词汇识别）
     * 注：在生产环境中建议使用专业的中文分词库如HanLP、jieba等
     */
    public static List<String> segmentText(String text) {
        List<String> words = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return words;
        }
        
        // 预处理文本
        text = preprocessText(text);
        
        // 1. 优先匹配常见词组和专业术语
        String remainingText = text;
        for (String phrase : COMMON_PHRASES) {
            if (remainingText.contains(phrase)) {
                words.add(phrase);
                remainingText = remainingText.replace(phrase, " "); // 用空格替换已匹配的词组
            }
        }
        
        // 按标点符号分割
        String[] segments = PUNCTUATION_PATTERN.split(remainingText);
        
        for (String segment : segments) {
            if (segment.trim().isEmpty()) {
                continue;
            }
            
            // 2. 提取中文词汇（改进版本）
            extractChineseWordsImproved(segment, words);
            
            // 3. 提取英文单词（改进版本）
            extractEnglishWordsImproved(segment, words);
            
            // 4. 提取数字和字母组合（如版本号、型号等）
            extractAlphanumericWords(segment, words);
        }
        
        return words;
    }
    
    /**
     * 提取中文词汇（改进版本）
     */
    private static void extractChineseWordsImproved(String text, List<String> words) {
        // 提取2-6字的中文词汇，扩大范围以捕获更多有意义的词汇
        for (int i = 0; i < text.length(); i++) {
            for (int len = 2; len <= Math.min(6, text.length() - i); len++) {
                String word = text.substring(i, i + len);
                if (CHINESE_PATTERN.matcher(word).matches() && word.length() >= 2) {
                    // 过滤掉纯重复字符的词汇
                    if (!isRepeatingCharacters(word)) {
                        words.add(word);
                    }
                }
            }
            
            // 提取有意义的单个中文字符（排除常见的无意义字符）
            if (i < text.length()) {
                String singleChar = text.substring(i, i + 1);
                if (CHINESE_PATTERN.matcher(singleChar).matches()) {
                    // 只保留有意义的单字
                    if (isMeaningfulSingleCharacter(singleChar)) {
                        words.add(singleChar);
                    }
                }
            }
        }
    }
    
    /**
     * 提取英文单词（改进版本）
     */
    private static void extractEnglishWordsImproved(String text, List<String> words) {
        String[] englishWords = text.split("\\s+");
        for (String word : englishWords) {
            word = word.toLowerCase().trim();
            if (ENGLISH_PATTERN.matcher(word).matches() && word.length() >= 2 && word.length() <= 20) {
                words.add(word);
            }
        }
    }
    
    /**
     * 提取数字和字母组合（如版本号、型号等）
     */
    private static void extractAlphanumericWords(String text, List<String> words) {
        Pattern alphanumericPattern = Pattern.compile("[a-zA-Z0-9]+[a-zA-Z0-9\\.-]*[a-zA-Z0-9]+");
        java.util.regex.Matcher alphanumericMatcher = alphanumericPattern.matcher(text);
        while (alphanumericMatcher.find()) {
            String word = alphanumericMatcher.group().toLowerCase();
            if (word.length() >= 2 && word.length() <= 15) {
                words.add(word);
            }
        }
    }
    
    /**
     * 检查是否为重复字符组成的词汇
     */
    private static boolean isRepeatingCharacters(String word) {
        if (word.length() <= 1) return false;
        char firstChar = word.charAt(0);
        for (int i = 1; i < word.length(); i++) {
            if (word.charAt(i) != firstChar) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断单个中文字符是否有意义
     */
    private static boolean isMeaningfulSingleCharacter(String character) {
        // 定义有意义的单字集合
        Set<String> meaningfulChars = new HashSet<>(Arrays.asList(
            "人", "事", "物", "时", "地", "情", "理", "法", "道", "德", "智", "勇", "仁", "义", "礼", "信",
            "爱", "恨", "喜", "怒", "哀", "乐", "美", "丑", "善", "恶", "真", "假", "新", "旧", "大", "小",
            "高", "低", "长", "短", "宽", "窄", "深", "浅", "厚", "薄", "重", "轻", "快", "慢", "强", "弱",
            "多", "少", "全", "空", "满", "缺", "有", "无", "生", "死", "始", "终", "前", "后", "左", "右",
            "上", "下", "内", "外", "中", "边", "正", "反", "顺", "逆", "进", "退", "升", "降", "开", "关",
            "成", "败", "得", "失", "来", "去", "动", "静", "明", "暗", "清", "浊", "冷", "热", "干", "湿",
            "硬", "软", "粗", "细", "尖", "钝", "圆", "方", "直", "弯", "平", "斜", "光", "影", "声", "色",
            "学", "教", "知", "识", "技", "能", "力", "量", "质", "品", "级", "等", "类", "种", "样", "式",
            "型", "形", "状", "态", "势", "位", "置", "点", "线", "面", "体", "积", "度", "数", "量", "值",
            "文", "字", "词", "句", "章", "节", "段", "篇", "书", "册", "本", "页", "行", "列", "排", "序",
            "水", "火", "土", "石", "金", "银", "铜", "铁", "钢", "木", "林", "森", "草", "花", "鸟", "兽",
            "年", "月", "日", "时", "分", "秒", "春", "夏", "秋", "冬", "早", "晚", "昼", "夜", "晨", "午",
            "红", "橙", "黄", "绿", "青", "蓝", "紫", "黑", "白", "灰", "棕", "粉", "银", "金", "铜", "铁"
        ));
        return meaningfulChars.contains(character);
    }
    
    /**
     * 过滤停用词和无意义词汇
     */
    public static List<String> filterStopWords(List<String> words) {
        return words.stream()
                .filter(word -> !STOP_WORDS.contains(word))
                .filter(word -> !NUMBER_PATTERN.matcher(word).matches()) // 过滤纯数字
                .filter(word -> word.length() >= 2) // 过滤单字符
                .filter(word -> !word.matches("^[a-zA-Z]$")) // 过滤单个英文字母
                .collect(Collectors.toList());
    }
    
    /**
     * 统计词频
     */
    public static Map<String, Integer> calculateWordFrequency(List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();
        
        for (String word : words) {
            if (word != null && !word.trim().isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        
        return wordCount;
    }
    
    /**
     * 获取高频词汇（按频次排序）
     */
    public static List<Map.Entry<String, Integer>> getTopWords(Map<String, Integer> wordCount, int limit) {
        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算词汇权重（增强版本，结合词频、位置、长度和语义重要性）
     */
    public static Map<String, Double> calculateWordWeights(Map<String, Integer> wordCount, int totalDocuments) {
        Map<String, Double> weights = new HashMap<>();
        int totalWords = wordCount.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();
            
            // 1. TF (Term Frequency) - 使用对数归一化
            double tf = 1.0 + Math.log(frequency);
            
            // 2. IDF (Inverse Document Frequency) - 改进版本
            double idf = Math.log((double) totalDocuments / (frequency + 1)) + 1.0;
            
            // 3. 长度权重（更长的词汇通常更有意义）
            double lengthWeight = calculateLengthWeight(word);
            
            // 4. 语义重要性权重
            double semanticWeight = calculateSemanticWeight(word);
            
            // 5. 词汇类型权重
            double typeWeight = calculateTypeWeight(word);
            
            // 6. 频率平衡权重（避免过度偏向高频词）
            double frequencyBalance = calculateFrequencyBalance(frequency, totalWords);
            
            // 综合权重计算
            double weight = tf * idf * lengthWeight * semanticWeight * typeWeight * frequencyBalance;
            
            weights.put(word, weight);
        }
        
        // 权重归一化
        return normalizeWeights(weights);
    }
    
    /**
     * 计算长度权重
     */
    private static double calculateLengthWeight(String word) {
        int length = word.length();
        if (length <= 1) return 0.5;
        if (length == 2) return 0.8;
        if (length == 3) return 1.0;
        if (length == 4) return 1.2;
        if (length >= 5) return 1.5;
        return 1.0;
    }
    
    /**
     * 计算语义重要性权重
     */
    private static double calculateSemanticWeight(String word) {
        // 专业术语和常见词组权重更高
        if (COMMON_PHRASES.contains(word)) {
            return 2.0;
        }
        
        // 技术相关词汇权重较高
        Set<String> techKeywords = new HashSet<>(Arrays.asList(
            "技术", "开发", "设计", "系统", "平台", "框架", "架构", "算法", "数据", "网络",
            "安全", "性能", "优化", "测试", "部署", "运维", "监控", "分析", "处理", "管理",
            "服务", "接口", "协议", "标准", "规范", "流程", "方案", "策略", "模式", "模型",
            "工具", "软件", "硬件", "设备", "产品", "项目", "团队", "用户", "客户", "市场"
        ));
        
        for (String keyword : techKeywords) {
            if (word.contains(keyword)) {
                return 1.5;
            }
        }
        
        // 动词和形容词权重适中
        Set<String> actionWords = new HashSet<>(Arrays.asList(
            "实现", "完成", "解决", "提升", "改进", "优化", "增强", "扩展", "集成", "部署",
            "测试", "验证", "分析", "设计", "开发", "构建", "创建", "生成", "处理", "管理",
            "配置", "安装", "运行", "执行", "操作", "控制", "监控", "维护", "更新", "升级"
        ));
        
        for (String action : actionWords) {
            if (word.contains(action)) {
                return 1.3;
            }
        }
        
        return 1.0;
    }
    
    /**
     * 计算词汇类型权重
     */
    private static double calculateTypeWeight(String word) {
        // 中英文混合词汇权重较高
        boolean hasEnglish = word.matches(".*[a-zA-Z].*");
        boolean hasChinese = word.matches(".*[\\u4e00-\\u9fa5].*");
        
        if (hasEnglish && hasChinese) {
            return 1.4;
        }
        
        // 纯英文技术词汇权重较高
        if (hasEnglish && !hasChinese) {
            return 1.2;
        }
        
        // 包含数字的词汇（版本号、型号等）权重适中
        if (word.matches(".*\\d.*")) {
            return 1.1;
        }
        
        return 1.0;
    }
    
    /**
     * 计算频率平衡权重（避免过度偏向高频词）
     */
    private static double calculateFrequencyBalance(int frequency, int totalWords) {
        double ratio = (double) frequency / totalWords;
        
        // 对于过于频繁的词汇，降低权重
        if (ratio > 0.1) {
            return 0.7;
        } else if (ratio > 0.05) {
            return 0.85;
        } else if (ratio > 0.02) {
            return 1.0;
        } else if (ratio > 0.005) {
            return 1.1;
        } else {
            return 1.2; // 稀有词汇权重稍高
        }
    }
    
    /**
     * 权重归一化
     */
    private static Map<String, Double> normalizeWeights(Map<String, Double> weights) {
        if (weights.isEmpty()) {
            return weights;
        }
        
        // 找到最大权重
        double maxWeight = weights.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        
        // 归一化到0-1范围
        Map<String, Double> normalizedWeights = new HashMap<>();
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            double normalizedWeight = entry.getValue() / maxWeight;
            normalizedWeights.put(entry.getKey(), normalizedWeight);
        }
        
        return normalizedWeights;
    }
    
    /**
     * 综合文本处理：从原始文本到词云数据
     */
    public static List<Object[]> processTextForWordCloud(List<String> texts, int limit) {
        List<String> allWords = new ArrayList<>();
        
        // 处理所有文本
        for (String text : texts) {
            List<String> words = segmentText(text);
            List<String> filteredWords = filterStopWords(words);
            allWords.addAll(filteredWords);
        }
        
        // 统计词频
        Map<String, Integer> wordCount = calculateWordFrequency(allWords);
        
        // 计算权重
        Map<String, Double> weights = calculateWordWeights(wordCount, texts.size());
        
        // 获取高频词并转换为词云格式
        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> {
                    // 按权重排序，如果权重相同则按频次排序
                    double weight1 = weights.getOrDefault(e1.getKey(), 0.0);
                    double weight2 = weights.getOrDefault(e2.getKey(), 0.0);
                    int weightCompare = Double.compare(weight2, weight1);
                    return weightCompare != 0 ? weightCompare : e2.getValue().compareTo(e1.getValue());
                })
                .limit(limit)
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());
    }
    
    /**
     * 文本相似度计算（基于余弦相似度）
     */
    public static double calculateTextSimilarity(String text1, String text2) {
        List<String> words1 = filterStopWords(segmentText(text1));
        List<String> words2 = filterStopWords(segmentText(text2));
        
        Map<String, Integer> freq1 = calculateWordFrequency(words1);
        Map<String, Integer> freq2 = calculateWordFrequency(words2);
        
        Set<String> allWords = new HashSet<>();
        allWords.addAll(freq1.keySet());
        allWords.addAll(freq2.keySet());
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (String word : allWords) {
            int count1 = freq1.getOrDefault(word, 0);
            int count2 = freq2.getOrDefault(word, 0);
            
            dotProduct += count1 * count2;
            norm1 += count1 * count1;
            norm2 += count2 * count2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
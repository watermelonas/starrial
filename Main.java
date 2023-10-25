import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Character {
    int level;
    double baseAttack;
    double baseSpeed;
    double baseCritRate;
    double baseCritDamage;
    double additionalAttack;
    double additionalCritRate;
    double additionalCritDamage;
    double quantumDamageIncrease;
    int skillPoints; // 战技点数
    boolean amplificationState; // 是否处于增幅状态
    boolean speedBoostState; // 是否处于速度提高状态
    double baseSkillDamageMultiplier; // 基本战技伤害倍率
    double baseUltimateDamageMultiplier; // 基本大招伤害倍率


    public Character(double baseAttack, double baseSpeed, double baseCritRate, double baseCritDamage) {
        this.level = 80;
        this.baseAttack = baseAttack;
        this.baseSpeed = baseSpeed;
        this.baseCritRate = baseCritRate;
        this.baseCritDamage = baseCritDamage;
        this.additionalAttack = 0;
        this.additionalCritRate = 0;
        this.additionalCritDamage = 0;
        this.quantumDamageIncrease = 0;
        this.skillPoints = 1000;
        this.amplificationState = false;
        this.speedBoostState = false;
        this.baseSkillDamageMultiplier = 2.2; // 战技造成220%攻击力量子伤害
        this.baseUltimateDamageMultiplier = 4.25; // 大招造成425%攻击力量子伤害
    }
    public Character(Character other) {
        this.baseAttack = other.baseAttack;
        this.baseSpeed = other.baseSpeed;
        this.baseCritRate = other.baseCritRate;
        this.baseCritDamage = other.baseCritDamage;
        this.additionalAttack = other.additionalAttack;
        this.additionalCritRate = other.additionalCritRate;
        this.additionalCritDamage = other.additionalCritDamage;
        this.quantumDamageIncrease = other.quantumDamageIncrease;
        this.skillPoints = other.skillPoints;
        this.amplificationState = other.amplificationState;
        this.speedBoostState = other.speedBoostState;
        this.baseSkillDamageMultiplier = other.baseSkillDamageMultiplier;
        this.baseUltimateDamageMultiplier = other.baseUltimateDamageMultiplier;
    }
    // 普攻
    public double normalAttackDamage() {
        skillPoints += 1; // 普攻回复1战技点
        return getTotalAttack(); // 攻击力100%量子伤害
    }

    // 战技
    public double skillAttackDamage() {
        if (skillPoints > 0) {
            skillPoints -= 1; // 战技消耗1战技点
            //todo
            speedBoostState = true; // 速度提高
            return getTotalAttack() * baseSkillDamageMultiplier;
        }
        return 0; // 如果没有战技点，战技无法使用
    }

    // 大招
    public double ultimateAttackDamage() {
        //todo
        //amplificationState = true; // 进入增幅状态
        return getTotalAttack() * baseUltimateDamageMultiplier;
    }

    // 检查增幅状态下的伤害加成
    public double getAmplificationMultiplier() {
        return amplificationState ? 1.8 : 1;
    }
    public double computeSkillAttackDamage() {
        return getTotalAttack() * baseSkillDamageMultiplier;
    }

    public double computeUltimateAttackDamage() {
        return getTotalAttack() * baseUltimateDamageMultiplier;
    }

    // 检查光锥被动加成
    public double getConePassiveCritRate(double targetHPPercentage) {
        return targetHPPercentage < 0.5 ? 16 : 0;
    }

    public double getTotalAttack() {
        return baseAttack + additionalAttack;
    }

    public double getTotalCritRate() {
        return Math.min(baseCritRate + additionalCritRate, 100.0);
    }


 /*   public double getTotalCritRate(double targetHPPercentage) {
        return baseCritRate + additionalCritRate + getConePassiveCritRate(targetHPPercentage);
    }*/
    public double getTotalCritDamage() {
        return baseCritDamage + additionalCritDamage;
    }
    public double computeDamageAgainst(Enemy enemy) {
        double skillMultiplier = 3 * this.computeSkillAttackDamage() + 1 * this.computeUltimateAttackDamage();

        double critMultiplier = (1 + (this.getTotalCritRate() / 100) * (this.getTotalCritDamage() / 100));
        double defenseMultiplier = (200 + 10 * this.level) / (enemy.getFinalDefense() + 200 + 10 * enemy.level);
        double vulnerabilityMultiplier = 1; // Assuming 1
        double unbrokenReduction = 1; // Assuming 1
        double resistanceMultiplier = 1; // Assuming 1
        //3* c.skillAttackDamage()+1* c.ultimateAttackDamage())*(1 + (c.getTotalCritRate() / 100) * (c.getTotalCritDamage() / 100) * 1.488
        return   skillMultiplier * getAmplificationMultiplier() * critMultiplier *1.488*defenseMultiplier*vulnerabilityMultiplier
                *unbrokenReduction*resistanceMultiplier;
       /* System.out.println("Skill Multiplier: " + skillMultiplier);
        System.out.println("Crit Multiplier: " + critMultiplier);
        System.out.println("Defense Multiplier: " + defenseMultiplier);*/

    }

    public void addRelicAttributes(List<Relic> relics) {
        for (Relic relic : relics) {
            this.additionalAttack += relic.fixedAttackIncrease + this.baseAttack * relic.percentAttackIncrease;
            this.additionalCritRate += relic.critRateIncrease;
            this.quantumDamageIncrease += relic.quantumDamageIncrease;
        }
    }
}
class Enemy {
    public  String name;
    public int level;
    public double baseAttack;
    public  double baseDefense;
    public  double baseHealth;
    public  double baseSpeed;
    public  double maxResilience;
    public  double attackModifier;
    public  double defenseModifier;
    public  double healthModifier;
    public  boolean hasQuantumResistance;
    public  boolean hasQuantumWeakness;
    public  int eliteGroup;

    // Constructor
    public Enemy(String name, int level, double baseAttack, double baseDefense, double baseHealth, double baseSpeed,
                 double maxResilience, double attackModifier, double defenseModifier, double healthModifier,
                 boolean hasQuantumResistance, boolean hasQuantumWeakness, int eliteGroup) {
        this.name = name;
        this.level = level;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseHealth = baseHealth;
        this.baseSpeed = baseSpeed;
        this.maxResilience = maxResilience;
        this.attackModifier = attackModifier;
        this.defenseModifier = defenseModifier;
        this.healthModifier = healthModifier;
        this.hasQuantumResistance = hasQuantumResistance;
        this.hasQuantumWeakness = hasQuantumWeakness;
        this.eliteGroup = eliteGroup;
    }

    // Getters and Setters omitted for brevity...

    public double getFinalAttack() {
        return baseAttack  * attackModifier * eliteGroup + baseAttack;
    }

    public double getFinalDefense() {
        return baseDefense * defenseModifier * eliteGroup + baseDefense;
    }

    public double getFinalHealth() {
        return baseHealth * healthModifier * eliteGroup + baseHealth;
    }

    public double getFinalSpeed() {
        return baseSpeed  * eliteGroup + baseSpeed;
    }

    @Override
    public String toString() {
        return "Enemy " + name + " [Attack: " + getFinalAttack() + ", Defense: " + getFinalDefense() +
                ", Health: " + getFinalHealth() + ", Speed: " + getFinalSpeed() + "]";
    }
}
class Relic {
    double fixedHPIncrease; // 增加的生命值
    double fixedAttackIncrease; // 增加的固定攻击力
    double percentAttackIncrease; // 增加的攻击力百分比
    double critRateIncrease; // 增加的暴击率
    double critDamageIncrease;
    double quantumDamageIncrease; // 增加的量子伤害百分比

    public Relic(double fixedHPIncrease, double fixedAttackIncrease, double percentAttackIncrease, double critRateIncrease,double critDamageIncrease, double quantumDamageIncrease) {
        this.fixedHPIncrease = fixedHPIncrease;
        this.fixedAttackIncrease = fixedAttackIncrease;
        this.percentAttackIncrease = percentAttackIncrease;
        this.critRateIncrease = critRateIncrease;
        this.critDamageIncrease = critDamageIncrease;
        this.quantumDamageIncrease = quantumDamageIncrease;

    }

    public void enhance(int times) {
        for (int i = 0; i < times; i++) {
            double random = Math.random(); // 你可以根据具体的强化概率调整这个随机数生成
            if (random < 0.33) {
                percentAttackIncrease += 3.5;
            } else if (random < 0.66) {
                critRateIncrease += 3;
            } else {
                critDamageIncrease += 5;
            }
        }
    }
}

public class Main {
    static  int TOTAL_ENHANCEMENTS = 30;
    //创建外宇宙之炎敌人
   // Enemy fire = new Enemy("Fire", 80, 18, 210, 930, 120, 10, 1, 1, 1, false, true, 1);
    public static double expectedDamage(Character c) {
        double skillMultiplier = 3 * c.skillAttackDamage() + 1 * c.ultimateAttackDamage();
        double critMultiplier = (1 + (c.getTotalCritRate() / 100) * (c.getTotalCritDamage() / 100));
        return  skillMultiplier * 1.8 * critMultiplier *1.488;   }//三个战技，一个大招，暴击，量子增伤
    public static double expectedDamage(Character c, Enemy e) {
        return c.computeDamageAgainst(e);
    }
    public static void main(String[] args) {

        Character bestCharacter = new Character(0, 0, 0, 0);  // 在这里初始化bestCharacter
        Enemy fire = new Enemy("Fire", 80, 18, 210, 930, 120, 10, 1, 1, 1, false, true, 1);
        double[][][] dp = new double[TOTAL_ENHANCEMENTS + 1][TOTAL_ENHANCEMENTS + 1][TOTAL_ENHANCEMENTS + 1];
        //Enemy fire = new Enemy("Fire", 80, 18, 210, 930, 120, 10, 1, 1, 1, false, true, 1);
        Scanner scanner = new Scanner(System.in);

        System.out.print("输入你目前的总攻击力或按回车跳过 ");
        String userTotalAttackInput = scanner.nextLine();
        System.out.print("输入你目前的暴击率或按回车跳过 ");
        String userTotalCritRateInput = scanner.nextLine();
        System.out.print("输入你目前的总暴击伤害或按回车跳过 ");
        String userTotalCritDamageInput = scanner.nextLine();
        System.out.print("输入你目前想强化的总次数或按回车跳过");
        String userTotalEnhancementsInput = scanner.nextLine();
        if (!userTotalEnhancementsInput.isEmpty()) {
            TOTAL_ENHANCEMENTS = Integer.parseInt(userTotalEnhancementsInput);
        }

        Character xier;
        if (userTotalAttackInput.isEmpty() || userTotalCritRateInput.isEmpty() || userTotalCritDamageInput.isEmpty() || userTotalEnhancementsInput.isEmpty()) {
            xier = new Character(1169, 115, 21, 174);  // 使用默认值
            List<Relic> xierRelics = new ArrayList<>();
            xierRelics.add(new Relic(1000, 0, 0, 0, 0,0)); // 遗器头
            xierRelics.add(new Relic(0, 352, 0, 0, 0,0)); // 遗器手
            xierRelics.add(new Relic(0, 0, 0.432, 0, 0,0)); // 遗器鞋
            xierRelics.add(new Relic(0, 0, 0, 32.4, 0,0)); // 暴击衣
            xierRelics.add(new Relic(0, 0, 0.432, 0, 0,0)); // 遗弃绳
            xierRelics.add(new Relic(0, 0, 0, 0, 0,0.488)); // 遗器球
            xierRelics.add(new Relic(0, 328, 0, 0, 0,0)); // 行迹
            // xierRelics.add(new Relic(0, 0, 0, 0, 63.8,0));//爆伤衣

            // 将遗器属性加到希儿的基本属性上
            for (Relic relic : xierRelics) {
                xier.additionalAttack += relic.fixedAttackIncrease + xier.baseAttack * relic.percentAttackIncrease; //刷新攻击
                xier.additionalCritRate += relic.critRateIncrease; //刷新暴击
                xier.additionalCritDamage += relic.critDamageIncrease; //刷新暴击率
                xier.quantumDamageIncrease += relic.quantumDamageIncrease; //刷新量子伤害
            }
        } else {
            double userTotalAttack = Double.parseDouble(userTotalAttackInput);
            double userTotalCritRate = Double.parseDouble(userTotalCritRateInput);
            double userTotalCritDamage = Double.parseDouble(userTotalCritDamageInput);

            xier = new Character(1169, 115, userTotalCritRate, userTotalCritDamage);  // 将总攻击力更改为默认值1169
           xier.additionalAttack += (userTotalAttack - 1169);  // 将用户输入的总攻击力和默认值的差值作为附加攻击力
        }

        int maxAttackEnhance = 0;
        int maxCritRateEnhance = 0;
        int maxCritDamageEnhance = 0;

        // 初始化
        double maxDamage = 0;
        for (int i = 0; i <= TOTAL_ENHANCEMENTS; i++) {
            for (int j = 0; j <= TOTAL_ENHANCEMENTS; j++) {
                for (int k = 0; k <= TOTAL_ENHANCEMENTS; k++) {
                    if (i + j + k > TOTAL_ENHANCEMENTS) continue;

                    Character temp = new Character(xier);  // 使用拷贝构造函数
                    //temp.addRelicAttributes(xierRelics);   // 加入遗弃属性
                    temp.additionalAttack += xier.baseAttack * i * 0.035;
                    temp.additionalCritRate += Math.min(j * 3, 100 - temp.baseCritRate);  // 确保暴击率不超过100%
                    temp.additionalCritDamage += k * 5;

                    dp[i][j][k] = expectedDamage(temp);
                    if (dp[i][j][k] > maxDamage) {
                        maxDamage = dp[i][j][k];
                        bestCharacter = temp;
                        //xier =temp;
                        //double damage = expectedDamage(xier, fire);
                        maxAttackEnhance = i;
                        maxCritRateEnhance = j;
                        maxCritDamageEnhance = k;
                    }
                }
            }
        }
       // Enemy fire = new Enemy("Fire", 80, 18, 210, 930, 120, 10, 1, 1, 1, false, true, 1);

        double damage = expectedDamage(xier, fire);
        System.out.println("Expected Damage: " + damage);

        System.out.println("希儿的属性如下：");
        System.out.println("攻击力: "+ xier.getTotalAttack());
        System.out.println("暴击率: "+ xier.getTotalCritRate());
        System.out.println("暴击伤害: "+ xier.getTotalCritDamage());
        System.out.println("增伤: "+ xier.getAmplificationMultiplier());


        System.out.println("最大伤害期望 " + maxDamage);
        System.out.println("攻击力加强次数 " + maxAttackEnhance + " times.");
        System.out.println("暴击率加强次数 " + maxCritRateEnhance + " times.");
        System.out.println("暴击伤害加强次数 " + maxCritDamageEnhance + " times.");
        System.out.println("总攻击力 " + bestCharacter.getTotalAttack());
        System.out.println("总暴击率 " + bestCharacter.getTotalCritRate());
        System.out.println("总暴击伤害 " + bestCharacter.getTotalCritDamage());
        System.out.println("增伤: "+ bestCharacter.getAmplificationMultiplier());
        //Enemy fire = new Enemy("Fire", 80, 18, 210, 930, 120, 10, 1, 1, 1, false, true, 1);


    }
}



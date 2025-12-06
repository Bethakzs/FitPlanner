# 📊 Nutrition Calculations Explained

## Input Fields (User Provides)

### 1. **weight** (Double)
- **Що це:** Вага користувача в кілограмах
- **Вимоги:** Мінімум 20 кг
- **Приклад:** 75.5 kg

### 2. **height** (Double)
- **Що це:** Зріст користувача в сантиметрах
- **Вимоги:** Мінімум 100 см
- **Приклад:** 175 cm

### 3. **gender** (Enum: MALE/FEMALE)
- **Що це:** Стать користувача
- **Використання:** Впливає на розрахунок BMR (чоловіки мають вищий базальний метаболізм)
- **Значення:**
  - `MALE` - чоловіча стать
  - `FEMALE` - жіноча стать

### 4. **activityLevel** (Enum: LOW/MEDIUM/HIGH)
- **Що це:** Рівень фізичної активності
- **Мультиплікатори:**
  - `LOW` (1.2) - Малорухливий спосіб життя (офісна робота, мало руху)
  - `MEDIUM` (1.55) - Помірна активність (3-5 тренувань на тиждень)
  - `HIGH` (1.9) - Висока активність (6-7 інтенсивних тренувань на тиждень)

### 5. **waterIntake** (Double)
- **Що це:** Поточне споживання води на день (літри)
- **Вимоги:** Не може бути негативним
- **Приклад:** 2.5 L

### 6. **allergies** (Set<AllergyType>)
- **Що це:** Список алергій користувача (опціонально)
- **Доступні значення:**
  - `GLUTEN` - непереносимість глютену
  - `LACTOSE` - непереносимість лактози
  - `NUTS` - алергія на горіхи
  - `EGGS` - алергія на яйця
  - `SEAFOOD` - алергія на морепродукти
  - `SOY` - алергія на сою

### 7. **saveReport** (Boolean)
- **Що це:** Чи зберігати звіт в історію
- **Значення:**
  - `true` - зберегти в базу даних
  - `false` - тільки показати результат (не зберігати)

---

## Calculated Fields (System Calculates)

### 1. **BMI** (Body Mass Index)
```java
BMI = weight (kg) / (height (m))²
```

**Приклад:**
- Weight: 75 kg
- Height: 175 cm = 1.75 m
- BMI = 75 / (1.75 × 1.75) = 75 / 3.0625 = **24.49**

**Що показує:** Співвідношення ваги до зросту, індикатор здорової ваги

---

### 2. **bmiCategory** (BMICategory Enum)

Категорія на основі BMI значення:

| BMI Range | Category | Description |
|-----------|----------|-------------|
| < 18.5 | `UNDERWEIGHT` | Недостатня вага |
| 18.5 - 24.9 | `NORMAL` | Нормальна вага |
| 25.0 - 29.9 | `OVERWEIGHT` | Надмірна вага |
| ≥ 30.0 | `OBESE` | Ожиріння |

**Логіка:**
```java
if (bmi < 18.5) return UNDERWEIGHT;
if (bmi < 25.0) return NORMAL;
if (bmi < 30.0) return OVERWEIGHT;
return OBESE;
```

---

### 3. **BMR** (Basal Metabolic Rate)

**Що це:** Базальний метаболізм - кількість калорій, яку тіло спалює в стані спокою

**Формула Mifflin-St Jeor:**

**Для чоловіків:**
```
BMR = 10 × weight(kg) + 6.25 × height(cm) - 5 × age + 5
```

**Для жінок:**
```
BMR = 10 × weight(kg) + 6.25 × height(cm) - 5 × age - 161
```

**У нашій реалізації:** Вік фіксований на 30 років (для MVP)

**Приклад (чоловік):**
- Weight: 75 kg
- Height: 175 cm
- Age: 30
- BMR = (10 × 75) + (6.25 × 175) - (5 × 30) + 5
- BMR = 750 + 1093.75 - 150 + 5 = **1698.75 kcal**

**Що показує:** Мінімальна кількість калорій для підтримки життєдіяльності організму

---

### 4. **TDEE** (Total Daily Energy Expenditure)

**Що це:** Загальні денні витрати енергії з урахуванням активності

**Формула:**
```
TDEE = BMR × Activity Multiplier
```

**Приклад:**
- BMR: 1699 kcal
- Activity Level: MEDIUM (1.55)
- TDEE = 1699 × 1.55 = **2633 kcal**

**Що показує:** Скільки калорій потрібно споживати для підтримки поточної ваги

---

### 5. **targetWeight** (Рекомендована вага)

**Що це:** Оптимальна вага на основі зросту та поточної категорії BMI

**Логіка:**
```java
Target BMI:
- UNDERWEIGHT → 20.0
- NORMAL → 22.0
- OVERWEIGHT/OBESE → 24.0
```

**Формула:**
```
targetWeight = targetBMI × (height(m))²
```

**Приклад:**
- Height: 175 cm = 1.75 m
- Current BMI Category: NORMAL → Target BMI = 22
- targetWeight = 22 × (1.75)² = 22 × 3.0625 = **67.38 kg**

**Що показує:** До якої ваги варто прагнути для здоров'я

---

### 6. **Macronutrients** (Макронутрієнти)

**Розподіл калорій:**
- Білки (Protein): 30% від TDEE
- Жири (Fats): 25% від TDEE
- Вуглеводи (Carbs): 45% від TDEE

**Калорійність на 1 грам:**
- Білки: 4 kcal/g
- Жири: 9 kcal/g
- Вуглеводи: 4 kcal/g

**Формули:**
```java
Protein (g) = (TDEE × 0.30) / 4
Fats (g) = (TDEE × 0.25) / 9
Carbs (g) = (TDEE × 0.45) / 4
```

**Приклад (TDEE = 2633 kcal):**
- **Protein:** (2633 × 0.30) / 4 = 789.9 / 4 = **197g**
- **Fats:** (2633 × 0.25) / 9 = 658.25 / 9 = **73g**
- **Carbs:** (2633 × 0.45) / 4 = 1184.85 / 4 = **296g**

**Що показує:** Скільки грамів білків, жирів і вуглеводів потрібно споживати щодня

---

### 7. **recommendedWater** (Рекомендоване споживання води)

**Базова формула:**
```
baseWater = weight(kg) × 0.033 liters
```

**Додаткова вода за активністю:**
- LOW: +0 L
- MEDIUM: +0.5 L
- HIGH: +1.0 L

**Формула:**
```java
recommendedWater = weight × 0.033 + activityBonus
```

**Приклад:**
- Weight: 75 kg
- Activity: MEDIUM
- recommendedWater = (75 × 0.033) + 0.5 = 2.475 + 0.5 = **2.98 L**

**Що показує:** Скільки літрів води рекомендується пити щодня

---

### 8. **waterRecommendation** (Статус води)

**Логіка:**
```java
difference = currentIntake - recommendedIntake

if (|difference| ≤ 0.2L) → Status: OPTIMAL
if (difference < 0) → Status: LOW
if (difference > 0) → Status: HIGH
```

**Приклади:**
- Current: 2.0L, Recommended: 2.1L → **OPTIMAL** (різниця 0.1L)
- Current: 1.5L, Recommended: 3.0L → **LOW** (не вистачає 1.5L)
- Current: 4.0L, Recommended: 2.5L → **HIGH** (перебір 1.5L)

**Що показує:** Чи достатньо користувач п'є води

---

## Response Fields Explained

### **bmiInterpretation** (String)
Текстове пояснення BMI категорії:
- UNDERWEIGHT: "Your BMI indicates underweight. Consider consulting a nutritionist."
- NORMAL: "Your BMI is in the healthy range. Maintain your current lifestyle!"
- OVERWEIGHT: "Your BMI indicates overweight. Consider a balanced diet and regular exercise."
- OBESE: "Your BMI indicates obesity. We recommend consulting a healthcare professional."

### **weightRecommendation** (String)
Персоналізована порада щодо ваги:
- Якщо NORMAL: "Your weight is optimal. Focus on maintaining..."
- Якщо інше: "To reach your target weight, you should lose/gain approximately X kg..."

### **activityRecommendations** (List<String>)
Список рекомендацій по фізичній активності на основі:
- Поточного рівня активності
- BMI категорії

**Приклад для LOW + OVERWEIGHT:**
- "Aim for at least 150 minutes of moderate aerobic activity per week"
- "Start with walking 30 minutes daily and gradually increase intensity"
- "Low-impact exercises like swimming or cycling are recommended"

### **dietaryRestrictions** (List<String>)
Список дієтичних обмежень на основі алергій:
- Якщо немає алергій: "No dietary restrictions detected..."
- Якщо є: Детальний список що уникати для кожної алергії

**Приклад для LACTOSE:**
- "- Lactose: Avoid dairy products or choose lactose-free alternatives"

### **generalTips** (List<String>)
Загальні поради для здоров'я:
- Базові (для всіх):
  - "Eat 4-5 small meals throughout the day..."
  - "Include protein in every meal..."
  - "Get 7-9 hours of quality sleep per night"
  
- Додаткові за BMI:
  - OVERWEIGHT/OBESE: "Create a moderate caloric deficit..."
  - UNDERWEIGHT: "Increase caloric intake with nutrient-dense foods..."

---

## Summary Table

| Field | Type | Source | Formula/Logic |
|-------|------|--------|---------------|
| weight | Double | User Input | - |
| height | Double | User Input | - |
| gender | Enum | User Input | - |
| activityLevel | Enum | User Input | - |
| waterIntake | Double | User Input | - |
| allergies | Set | User Input | - |
| BMI | Double | Calculated | weight / (height/100)² |
| bmiCategory | Enum | Calculated | Based on BMI ranges |
| BMR | Double | Calculated | Mifflin-St Jeor formula |
| TDEE | Double | Calculated | BMR × activity multiplier |
| targetWeight | Double | Calculated | targetBMI × height² |
| dailyProtein | Double | Calculated | (TDEE × 0.30) / 4 |
| dailyFats | Double | Calculated | (TDEE × 0.25) / 9 |
| dailyCarbs | Double | Calculated | (TDEE × 0.45) / 4 |
| recommendedWater | Double | Calculated | weight × 0.033 + bonus |

---

## Example Full Calculation

**Input:**
```json
{
  "weight": 75,
  "height": 175,
  "gender": "MALE",
  "activityLevel": "MEDIUM",
  "waterIntake": 2.0,
  "allergies": ["LACTOSE"]
}
```

**Step-by-step:**

1. **BMI:** 75 / (1.75)² = **24.49**
2. **Category:** NORMAL (18.5 ≤ 24.49 < 25)
3. **BMR:** (10×75) + (6.25×175) - (5×30) + 5 = **1698.75 kcal**
4. **TDEE:** 1698.75 × 1.55 = **2633 kcal**
5. **Target Weight:** 22 × (1.75)² = **67.38 kg**
6. **Protein:** (2633 × 0.30) / 4 = **197g**
7. **Fats:** (2633 × 0.25) / 9 = **73g**
8. **Carbs:** (2633 × 0.45) / 4 = **296g**
9. **Water:** (75 × 0.033) + 0.5 = **2.98L**
10. **Water Status:** 2.0 < 2.98 → **LOW** (need +0.98L)

**Output:**
```json
{
  "bmi": 24.49,
  "bmiCategory": "NORMAL",
  "bmr": 1699,
  "tdee": 2633,
  "targetWeight": 67.38,
  "macronutrients": {
    "dailyCalories": 2633,
    "protein": 197,
    "fats": 73,
    "carbs": 296
  },
  "waterRecommendation": {
    "currentIntake": 2.0,
    "recommendedIntake": 2.98,
    "status": "LOW",
    "advice": "Try to drink 1.0 more liters of water daily."
  }
}
```

---

## Implementation Location

**Backend файли:**
- `NutritionServiceImpl.java` - всі розрахунки
- `UserMetricsRequest.java` - вхідні дані
- `NutritionReportResponse.java` - результати

**Методи розрахунків:**
```java
calculateBMI(weight, height)           // BMI
determineBMICategory(bmi)              // Category
calculateBMR(weight, height, gender)   // BMR
calculateTDEE(bmr, activityMultiplier) // TDEE
calculateTargetWeight(height, category) // Target
calculateRecommendedWater(weight, activity) // Water
```

---

**Створено:** December 2025  
**Версія:** 1.0 (MVP)


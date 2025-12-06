import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { nutritionAPI, authAPI } from '../services/api';

function Dashboard() {
    const [formData, setFormData] = useState({
        weight: '',
        height: '',
        gender: 'MALE',
        activityLevel: 'MEDIUM',
        waterIntake: '',
        allergies: [],
        saveReport: false
    });
    const [report, setReport] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleAllergyChange = (allergy) => {
        const allergies = formData.allergies.includes(allergy)
            ? formData.allergies.filter(a => a !== allergy)
            : [...formData.allergies, allergy];
        setFormData({ ...formData, allergies });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        
        try {
            const payload = {
                ...formData,
                weight: parseFloat(formData.weight),
                height: parseFloat(formData.height),
                waterIntake: parseFloat(formData.waterIntake)
            };
            const response = await nutritionAPI.generateReport(payload);
            setReport(response.data);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to generate report');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        try {
            await authAPI.logout();
        } catch (err) {
            console.error('Logout error:', err);
        }
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <div className="header-content">
                    <h1>🥗 Sonya Nutrition</h1>
                    <div className="header-actions">
                        <button onClick={() => navigate('/history')} className="btn-secondary">
                            📊 View History
                        </button>
                        <button onClick={handleLogout} className="btn-logout">
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            <div className="dashboard-content">
                <div className="form-section">
                    <h2>Generate Nutrition Report</h2>
                    {error && <div className="error">{error}</div>}
                    
                    <form onSubmit={handleSubmit}>
                        <div className="form-row">
                            <input
                                type="number"
                                name="weight"
                                placeholder="Weight (kg)"
                                value={formData.weight}
                                onChange={handleChange}
                                required
                                min="20"
                                step="0.1"
                            />
                            <input
                                type="number"
                                name="height"
                                placeholder="Height (cm)"
                                value={formData.height}
                                onChange={handleChange}
                                required
                                min="100"
                                step="0.1"
                            />
                        </div>

                        <select name="gender" value={formData.gender} onChange={handleChange}>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </select>

                        <select name="activityLevel" value={formData.activityLevel} onChange={handleChange}>
                            <option value="LOW">Low Activity</option>
                            <option value="MEDIUM">Medium Activity</option>
                            <option value="HIGH">High Activity</option>
                        </select>

                        <input
                            type="number"
                            name="waterIntake"
                            placeholder="Water Intake (liters)"
                            value={formData.waterIntake}
                            onChange={handleChange}
                            required
                            min="0"
                            step="0.1"
                        />

                        <div className="allergies-section">
                            <label>Allergies (optional):</label>
                            <div className="checkbox-group">
                                {['GLUTEN', 'LACTOSE', 'NUTS', 'EGGS', 'SEAFOOD', 'SOY'].map(allergy => (
                                    <label key={allergy} className="checkbox-label">
                                        <input
                                            type="checkbox"
                                            checked={formData.allergies.includes(allergy)}
                                            onChange={() => handleAllergyChange(allergy)}
                                        />
                                        {allergy}
                                    </label>
                                ))}
                            </div>
                        </div>

                        <label className="checkbox-label save-report">
                            <input
                                type="checkbox"
                                name="saveReport"
                                checked={formData.saveReport}
                                onChange={handleChange}
                            />
                            Save this report to history
                        </label>

                        <button type="submit" disabled={loading}>
                            {loading ? 'Generating...' : 'Generate Report'}
                        </button>
                    </form>
                </div>

                {report && (
                    <div className="report-section">
                        <h2>Your Nutrition Report</h2>
                        
                        <div className="report-card">
                            <h3>Body Metrics</h3>
                            <p><strong>BMI:</strong> {report.bmi} ({report.bmiCategory})</p>
                            <p>{report.bmiInterpretation}</p>
                            <p><strong>Target Weight:</strong> {report.targetWeight} kg</p>
                            <p>{report.weightRecommendation}</p>
                        </div>

                        <div className="report-card">
                            <h3>Daily Caloric Needs</h3>
                            <p><strong>BMR:</strong> {report.bmr} kcal</p>
                            <p><strong>TDEE:</strong> {report.tdee} kcal</p>
                        </div>

                        <div className="report-card">
                            <h3>🍽️ Daily Nutrition Plan</h3>
                            <p className="daily-calories">
                                <strong>Daily Calories:</strong> {report.macronutrients.dailyCalories} kcal
                            </p>
                            
                            {report.foodRecommendations && (
                                <div className="nutrition-grid">
                                    <div className="macro-section">
                                        <div className="macro-header protein">
                                            <span className="macro-icon">🍗</span>
                                            <div className="macro-info">
                                                <h4>Protein</h4>
                                                <span className="macro-amount">{report.macronutrients.protein}g</span>
                                                <span className="macro-percentage">({report.macronutrients.proteinPercentage})</span>
                                            </div>
                                        </div>
                                        <div className="food-items">
                                            {report.foodRecommendations.proteinFoods.map((food, idx) => (
                                                <span key={idx} className="food-tag protein">{food}</span>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="macro-section">
                                        <div className="macro-header fats">
                                            <span className="macro-icon">🥑</span>
                                            <div className="macro-info">
                                                <h4>Healthy Fats</h4>
                                                <span className="macro-amount">{report.macronutrients.fats}g</span>
                                                <span className="macro-percentage">({report.macronutrients.fatsPercentage})</span>
                                            </div>
                                        </div>
                                        <div className="food-items">
                                            {report.foodRecommendations.fatFoods.map((food, idx) => (
                                                <span key={idx} className="food-tag fats">{food}</span>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="macro-section">
                                        <div className="macro-header carbs">
                                            <span className="macro-icon">🌾</span>
                                            <div className="macro-info">
                                                <h4>Carbohydrates</h4>
                                                <span className="macro-amount">{report.macronutrients.carbs}g</span>
                                                <span className="macro-percentage">({report.macronutrients.carbsPercentage})</span>
                                            </div>
                                        </div>
                                        <div className="food-items">
                                            {report.foodRecommendations.carbFoods.map((food, idx) => (
                                                <span key={idx} className="food-tag carbs">{food}</span>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div className="report-card">
                            <h3>Water Recommendation</h3>
                            <p><strong>Current:</strong> {report.waterRecommendation.currentIntake}L</p>
                            <p><strong>Recommended:</strong> {report.waterRecommendation.recommendedIntake}L</p>
                            <p><strong>Status:</strong> {report.waterRecommendation.status}</p>
                            <p>{report.waterRecommendation.advice}</p>
                        </div>

                        <div className="report-card">
                            <h3>Activity Recommendations</h3>
                            <ul>
                                {report.activityRecommendations.map((rec, idx) => (
                                    <li key={idx}>{rec}</li>
                                ))}
                            </ul>
                        </div>

                        {report.allergies && report.allergies.length > 0 && (
                            <div className="report-card">
                                <h3>Dietary Restrictions</h3>
                                <ul>
                                    {report.dietaryRestrictions.map((restriction, idx) => (
                                        <li key={idx}>{restriction}</li>
                                    ))}
                                </ul>
                            </div>
                        )}

                        <div className="report-card">
                            <h3>General Tips</h3>
                            <ul>
                                {report.generalTips.map((tip, idx) => (
                                    <li key={idx}>{tip}</li>
                                ))}
                            </ul>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;


import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, userAPI, systemAPI, dietAPI, recommendationAPI, nutritionAPI } from '../services/api';

function Dashboard() {
    const [activeTab, setActiveTab] = useState('nutrition');
    const [formData, setFormData] = useState({
        weight: '',
        height: '',
        age: '',
        gender: 'MALE',
        activityLevel: 'MEDIUM',
        waterIntake: '',
        allergies: [],
        saveReport: false
    });
    const [currentReport, setCurrentReport] = useState(null);
    const [reports, setReports] = useState([]);
    const [diets, setDiets] = useState([]);
    const [recommendations, setRecommendations] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState('');
    const [tooltipVisible, setTooltipVisible] = useState(null);
    const navigate = useNavigate();

    const tooltips = {
        bmi: "Body Mass Index (BMI) - A measure of body fat based on height and weight. Ranges: <18.5 Underweight, 18.5-24.9 Normal, 25-29.9 Overweight, ≥30 Obese",
        bmr: "Basal Metabolic Rate (BMR) - The number of calories your body needs at rest to maintain vital functions (breathing, circulation, cell production)",
        tdee: "Total Daily Energy Expenditure (TDEE) - Total calories you burn per day including physical activity. TDEE = BMR × Activity Factor",
        activityLevel: "Activity Level affects calorie needs: Low (1.2) - sedentary, Medium (1.55) - moderate exercise 3-5 days/week, High (1.9) - intense exercise 6-7 days/week",
        allergies: "Select any food allergies to exclude those ingredients from your diet recommendations",
        protein: "Proteins - Essential for muscle growth and repair. Recommended: 10-35% of total calories",
        fats: "Fats - Important for hormone production and nutrient absorption. Recommended: 20-35% of total calories",
        carbs: "Carbohydrates - Primary energy source for your body and brain. Recommended: 45-65% of total calories"
    };

    const InfoIcon = ({ type }) => (
        <span 
            className="info-icon"
            onMouseEnter={() => setTooltipVisible(type)}
            onMouseLeave={() => setTooltipVisible(null)}
        >
            ℹ️
            {tooltipVisible === type && (
                <span className="tooltip">{tooltips[type]}</span>
            )}
        </span>
    );

    useEffect(() => {
        loadUserData();
        loadReports();
    }, []);

    const loadUserData = async () => {
        try {
            const response = await userAPI.getCurrentUser();
            if (response.data) {
                setFormData(prev => ({
                    ...prev,
                    weight: response.data.weight || '',
                    height: response.data.height || '',
                    age: response.data.age || '',
                    gender: response.data.gender || 'MALE',
                    activityLevel: response.data.activityLevel || 'MEDIUM'
                }));
            }
        } catch (err) {
            console.error('Failed to load user data:', err);
        }
    };

    const loadReports = async () => {
        try {
            const response = await nutritionAPI.getReports();
            setReports(response.data);
        } catch (err) {
            console.error('Failed to load reports:', err);
        }
    };

    const loadReportData = async (reportId) => {
        try {
            const [dietsRes, recommendationsRes] = await Promise.all([
                dietAPI.getUserDiets(),
                recommendationAPI.getUserRecommendations()
            ]);
            setDiets(dietsRes.data);
            setRecommendations(recommendationsRes.data);
        } catch (err) {
            console.error('Failed to load report data:', err);
        }
    };

    const handleViewReport = async (report) => {
        setCurrentReport(report);
        await loadReportData();
        setActiveTab('report-view');
    };

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

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        
        try {
            const payload = {
                age: parseInt(formData.age),
                weight: parseFloat(formData.weight),
                height: parseFloat(formData.height),
                gender: formData.gender,
                activityLevel: formData.activityLevel
            };
            await userAPI.updateCurrentUser(payload);
            setSuccess('Profile updated successfully!');
            loadUserData();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update profile');
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateReport = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        
        try {
            const payload = {
                ...formData,
                age: parseInt(formData.age),
                weight: parseFloat(formData.weight),
                height: parseFloat(formData.height),
                waterIntake: parseFloat(formData.waterIntake),
                saveReport: true
            };
            const response = await systemAPI.saveResults(payload);
            setCurrentReport(response.data);
            setSuccess('✅ Report, diet, and recommendations generated successfully!');
            
            await loadReportData();
            await loadReports();
            setActiveTab('report-view');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to generate report');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteReport = async (reportId) => {
        if (!window.confirm('Are you sure you want to delete this report?')) return;
        
        try {
            await nutritionAPI.deleteReport(reportId);
            setSuccess('✅ Report deleted successfully!');
            await loadReports();
            if (currentReport?.reportId === reportId) {
                setCurrentReport(null);
                setActiveTab('nutrition');
            }
        } catch (err) {
            setError('Failed to delete report');
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
                    <h1>🥗Fit Planner</h1>
                    <div className="header-actions">
                        <button onClick={handleLogout} className="btn-logout">
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            <div className="dashboard-tabs">
                <button 
                    className={activeTab === 'profile' ? 'active' : ''} 
                    onClick={() => setActiveTab('profile')}
                >
                    👤 Profile
                </button>
                <button 
                    className={activeTab === 'nutrition' ? 'active' : ''} 
                    onClick={() => setActiveTab('nutrition')}
                >
                    📊 Generate Report
                </button>
                <button 
                    className={activeTab === 'history' ? 'active' : ''} 
                    onClick={() => setActiveTab('history')}
                >
                    📚 History
                </button>
                {currentReport && (
                    <button 
                        className={activeTab === 'report-view' ? 'active' : ''} 
                        onClick={() => setActiveTab('report-view')}
                    >
                        📈 Current Report
                    </button>
                )}
            </div>

            <div className="dashboard-content">
                {error && <div className="error">{error}</div>}
                {success && <div className="success">{success}</div>}

                {activeTab === 'profile' && (
                    <div className="tab-content">
                        <h2>Update Your Profile</h2>
                        <form onSubmit={handleUpdateProfile} className="profile-form">
                            <div className="form-row">
                                <input
                                    type="number"
                                    name="age"
                                    placeholder="Age"
                                    value={formData.age}
                                    onChange={handleChange}
                                    required
                                    min="1"
                                />
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
                            </div>
                            <div className="form-row">
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
                                <select name="gender" value={formData.gender} onChange={handleChange}>
                                    <option value="MALE">Male</option>
                                    <option value="FEMALE">Female</option>
                                </select>
                            </div>
                            <div className="form-field-with-info">
                                <select name="activityLevel" value={formData.activityLevel} onChange={handleChange}>
                                    <option value="LOW">Low Activity</option>
                                    <option value="MEDIUM">Medium Activity</option>
                                    <option value="HIGH">High Activity</option>
                                </select>
                                <InfoIcon type="activityLevel" />
                            </div>
                            <button type="submit" disabled={loading}>
                                {loading ? 'Updating...' : 'Update Profile'}
                            </button>
                        </form>
                    </div>
                )}

                {activeTab === 'nutrition' && (
                    <div className="tab-content">
                        <h2>Generate Full Nutrition Report</h2>
                        <form onSubmit={handleGenerateReport}>
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
                                <label>
                                    Allergies (optional): <InfoIcon type="allergies" />
                                </label>
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

                            <button type="submit" disabled={loading}>
                                {loading ? 'Generating...' : 'Generate Complete Report (with Diet & Recommendations)'}
                            </button>
                        </form>
                        <p className="info-text">
                            ℹ️ This will automatically create: nutrition report + personalized diet + exercise recommendations
                        </p>
                    </div>
                )}

                {activeTab === 'history' && (
                    <div className="tab-content">
                        <h2>📚 Reports History</h2>
                        {reports.length === 0 ? (
                            <div className="empty-state">
                                <p>No reports yet. Generate your first nutrition report!</p>
                                <button onClick={() => setActiveTab('nutrition')}>
                                    📊 Generate Report
                                </button>
                            </div>
                        ) : (
                            <div className="reports-list">
                                {reports.map(report => (
                                    <div key={report.reportId} className="report-card">
                                        <div className="card-header">
                                            <h3>Report from {new Date(report.createdAt).toLocaleDateString()}</h3>
                                            <div className="card-actions">
                                                <button 
                                                    onClick={() => handleViewReport(report)}
                                                    className="btn-view"
                                                >
                                                    👁️ View
                                                </button>
                                                <button 
                                                    onClick={() => handleDeleteReport(report.reportId)}
                                                    className="btn-delete"
                                                    title="Delete entire report with diet and recommendations"
                                                >
                                                    🗑️ Delete
                                                </button>
                                            </div>
                                        </div>
                                        <div className="report-summary">
                                            <p><strong>BMI:</strong> {report.bmi?.toFixed(1)} ({report.bmiCategory})</p>
                                            <p><strong>Weight:</strong> {report.weight} kg | <strong>Height:</strong> {report.height} cm</p>
                                            <p><strong>Daily Calories:</strong> {report.macronutrients?.dailyCalories} kcal</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {activeTab === 'report-view' && currentReport && (
                    <div className="report-section">
                        <h2>Nutrition Report</h2>
                        <p className="report-date">Generated: {new Date(currentReport.createdAt).toLocaleString()}</p>
                        
                        <div className="report-card">
                            <h3>Body Metrics <InfoIcon type="bmi" /></h3>
                            <p><strong>BMI:</strong> {currentReport.bmi?.toFixed(1)} ({currentReport.bmiCategory})</p>
                            <p>{currentReport.bmiInterpretation}</p>
                            <p><strong>Target Weight:</strong> {currentReport.targetWeight} kg</p>
                            <p>{currentReport.weightRecommendation}</p>
                        </div>

                        <div className="report-card">
                            <h3>Daily Caloric Needs</h3>
                            <p><strong>BMR:</strong> {currentReport.bmr} kcal <InfoIcon type="bmr" /></p>
                            <p><strong>TDEE:</strong> {currentReport.tdee} kcal <InfoIcon type="tdee" /></p>
                        </div>

                        {diets && diets.length > 0 && (
                            <div className="report-card">
                                <h3>🍽️ Recommended Diet Plan</h3>
                                {(() => {
                                    const latestDiet = diets[0];
                                    return (
                                        <div className="diet-display">
                                            <div className="macros-grid">
                                                <div className="macro-item protein">
                                                    <span className="macro-icon">🍗</span>
                                                    <div>
                                                        <strong>Protein</strong>
                                                        <span>{latestDiet.totalProtein?.toFixed(1)}g</span>
                                                    </div>
                                                </div>
                                                <div className="macro-item fats">
                                                    <span className="macro-icon">🥑</span>
                                                    <div>
                                                        <strong>Fats</strong>
                                                        <span>{latestDiet.totalFats?.toFixed(1)}g</span>
                                                    </div>
                                                </div>
                                                <div className="macro-item carbs">
                                                    <span className="macro-icon">🌾</span>
                                                    <div>
                                                        <strong>Carbs</strong>
                                                        <span>{latestDiet.totalCarbs?.toFixed(1)}g</span>
                                                    </div>
                                                </div>
                                                <div className="macro-item total">
                                                    <span className="macro-icon">🔥</span>
                                                    <div>
                                                        <strong>Total</strong>
                                                        <span>{latestDiet.totalCalories?.toFixed(0)} kcal</span>
                                                    </div>
                                                </div>
                                            </div>
                                            {latestDiet.products && latestDiet.products.length > 0 && (
                                                <div className="diet-products">
                                                    <h4>Diet Composition:</h4>
                                                    {(() => {
                                                        // Group products by dominant macronutrient
                                                        const proteins = latestDiet.products.filter(p => 
                                                            p.protein > p.fats && p.protein > p.carbohydrates
                                                        );
                                                        const fats = latestDiet.products.filter(p => 
                                                            p.fats > p.protein && p.fats > p.carbohydrates
                                                        );
                                                        const carbs = latestDiet.products.filter(p => 
                                                            p.carbohydrates > p.protein && p.carbohydrates > p.fats
                                                        );

                                                        return (
                                                            <>
                                                                {proteins.length > 0 && (
                                                                    <div className="product-category">
                                                                        <h5 className="category-title protein-category">
                                                                            <span className="category-icon">🍗</span>
                                                                            Protein Sources
                                                                        </h5>
                                                                        <div className="products-grid">
                                                                            {proteins.map((product, idx) => (
                                                                                <div key={idx} className="product-item-card">
                                                                                    <strong>{product.name}</strong>
                                                                                    <span className="calories">{product.calories} kcal</span>
                                                                                    <span className="macros-small">
                                                                                        P: {product.protein}g | F: {product.fats}g | C: {product.carbohydrates}g
                                                                                    </span>
                                                                                </div>
                                                                            ))}
                                                                        </div>
                                                                    </div>
                                                                )}

                                                                {fats.length > 0 && (
                                                                    <div className="product-category">
                                                                        <h5 className="category-title fats-category">
                                                                            <span className="category-icon">🥑</span>
                                                                            Healthy Fats
                                                                        </h5>
                                                                        <div className="products-grid">
                                                                            {fats.map((product, idx) => (
                                                                                <div key={idx} className="product-item-card">
                                                                                    <strong>{product.name}</strong>
                                                                                    <span className="calories">{product.calories} kcal</span>
                                                                                    <span className="macros-small">
                                                                                        P: {product.protein}g | F: {product.fats}g | C: {product.carbohydrates}g
                                                                                    </span>
                                                                                </div>
                                                                            ))}
                                                                        </div>
                                                                    </div>
                                                                )}

                                                                {carbs.length > 0 && (
                                                                    <div className="product-category">
                                                                        <h5 className="category-title carbs-category">
                                                                            <span className="category-icon">🌾</span>
                                                                            Carbohydrates
                                                                        </h5>
                                                                        <div className="products-grid">
                                                                            {carbs.map((product, idx) => (
                                                                                <div key={idx} className="product-item-card">
                                                                                    <strong>{product.name}</strong>
                                                                                    <span className="calories">{product.calories} kcal</span>
                                                                                    <span className="macros-small">
                                                                                        P: {product.protein}g | F: {product.fats}g | C: {product.carbohydrates}g
                                                                                    </span>
                                                                                </div>
                                                                            ))}
                                                                        </div>
                                                                    </div>
                                                                )}
                                                            </>
                                                        );
                                                    })()}
                                                </div>
                                            )}
                                        </div>
                                    );
                                })()}
                            </div>
                        )}

                        <div className="report-card">
                            <h3>📋 Activity Recommendations</h3>
                            <ul>
                                {currentReport.activityRecommendations?.map((rec, idx) => (
                                    <li key={idx}>{rec}</li>
                                ))}
                            </ul>
                        </div>

                        <div className="report-card">
                            <h3>💡 General Tips</h3>
                            <ul>
                                {currentReport.generalTips?.map((tip, idx) => (
                                    <li key={idx}>{tip}</li>
                                ))}
                            </ul>
                        </div>


                        {recommendations && recommendations.length > 0 && (
                            <div className="report-card recommendations-section">
                                <h3>💪 Exercise Recommendations</h3>
                                {(() => {
                                    const latestRec = recommendations[0];
                                    return (
                                        <div className="recommendation-display">
                                            <p className="description">{latestRec.description}</p>
                                            {latestRec.exercises && latestRec.exercises.length > 0 && (
                                                <div className="exercises">
                                                    <h4>Recommended Exercises:</h4>
                                                    <div className="exercises-grid">
                                                        {latestRec.exercises.map(ex => (
                                                            <div key={ex.id} className="exercise-card">
                                                                <strong>{ex.name}</strong>
                                                                <div className="exercise-details">
                                                                    <span className="exercise-type">{ex.type}</span>
                                                                    {ex.durationMinutes && <span>⏱️ {ex.durationMinutes} min</span>}
                                                                    {ex.caloriesBurned && <span>🔥 {ex.caloriesBurned} kcal</span>}
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })()}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;

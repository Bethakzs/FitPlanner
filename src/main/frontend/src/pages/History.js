import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { nutritionAPI, authAPI } from '../services/api';

function History() {
    const [reports, setReports] = useState([]);
    const [selectedReport, setSelectedReport] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadReports();
    }, []);

    const loadReports = async () => {
        try {
            const response = await nutritionAPI.getReports();
            setReports(response.data);
        } catch (err) {
            setError('Failed to load reports');
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
                    <h1>📊 Report History</h1>
                    <div className="header-actions">
                        <button onClick={() => navigate('/dashboard')} className="btn-secondary">
                            ← Back to Dashboard
                        </button>
                        <button onClick={handleLogout} className="btn-logout">
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            <div className="history-container">
                {loading && <div className="loading">Loading reports...</div>}
                {error && <div className="error">{error}</div>}

                {!loading && reports.length === 0 && (
                    <div className="empty-state">
                        <h2>No Reports Yet</h2>
                        <p>Generate and save your first nutrition report to see it here.</p>
                        <button onClick={() => navigate('/dashboard')} className="btn-primary">
                            Create Report
                        </button>
                    </div>
                )}

                {!loading && reports.length > 0 && (
                    <div className="history-grid">
                        <div className="reports-list">
                            <h2>Your Reports ({reports.length})</h2>
                            {reports.map((report) => (
                                <div
                                    key={report.reportId}
                                    className={`report-card ${selectedReport?.reportId === report.reportId ? 'active' : ''}`}
                                    onClick={() => setSelectedReport(report)}
                                >
                                    <div className="report-card-header">
                                        <span className="report-date">
                                            {new Date(report.createdAt).toLocaleDateString('en-US', {
                                                month: 'short',
                                                day: 'numeric',
                                                year: 'numeric',
                                                hour: '2-digit',
                                                minute: '2-digit'
                                            })}
                                        </span>
                                        <span className={`bmi-badge ${report.bmiCategory.toLowerCase()}`}>
                                            {report.bmiCategory}
                                        </span>
                                    </div>
                                    <div className="report-card-stats">
                                        <div className="stat">
                                            <span className="stat-label">Weight</span>
                                            <span className="stat-value">{report.weight} kg</span>
                                        </div>
                                        <div className="stat">
                                            <span className="stat-label">BMI</span>
                                            <span className="stat-value">{report.bmi}</span>
                                        </div>
                                        <div className="stat">
                                            <span className="stat-label">TDEE</span>
                                            <span className="stat-value">{report.tdee} kcal</span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {selectedReport && (
                            <div className="report-details">
                                <div className="details-header">
                                    <h2>Report Details</h2>
                                    <button onClick={() => setSelectedReport(null)} className="btn-close">
                                        ✕
                                    </button>
                                </div>

                                <div className="details-content">
                                    <section className="detail-section">
                                        <h3>📏 Body Metrics</h3>
                                        <div className="metrics-grid">
                                            <div className="metric">
                                                <span className="metric-label">Weight</span>
                                                <span className="metric-value">{selectedReport.weight} kg</span>
                                            </div>
                                            <div className="metric">
                                                <span className="metric-label">Height</span>
                                                <span className="metric-value">{selectedReport.height} cm</span>
                                            </div>
                                            <div className="metric">
                                                <span className="metric-label">BMI</span>
                                                <span className="metric-value">{selectedReport.bmi}</span>
                                            </div>
                                            <div className="metric">
                                                <span className="metric-label">Target Weight</span>
                                                <span className="metric-value">{selectedReport.targetWeight} kg</span>
                                            </div>
                                        </div>
                                        <p className="interpretation">{selectedReport.bmiInterpretation}</p>
                                    </section>

                                    <section className="detail-section">
                                        <h3>🔥 Caloric Needs</h3>
                                        <div className="metrics-grid">
                                            <div className="metric">
                                                <span className="metric-label">BMR</span>
                                                <span className="metric-value">{selectedReport.bmr} kcal</span>
                                            </div>
                                            <div className="metric">
                                                <span className="metric-label">TDEE</span>
                                                <span className="metric-value">{selectedReport.tdee} kcal</span>
                                            </div>
                                        </div>
                                    </section>

                                    <section className="detail-section">
                                        <h3>🍽️ Daily Nutrition Plan</h3>
                                        <p className="daily-calories">
                                            <strong>Daily Calories:</strong> {selectedReport.macronutrients.dailyCalories} kcal
                                        </p>
                                        
                                        {selectedReport.foodRecommendations && (
                                            <div className="nutrition-grid">
                                                <div className="macro-section">
                                                    <div className="macro-header protein">
                                                        <span className="macro-icon">🍗</span>
                                                        <div className="macro-info">
                                                            <h4>Protein</h4>
                                                            <span className="macro-amount">{selectedReport.macronutrients.protein}g</span>
                                                            <span className="macro-percentage">({selectedReport.macronutrients.proteinPercentage})</span>
                                                        </div>
                                                    </div>
                                                    <div className="food-items">
                                                        {selectedReport.foodRecommendations.proteinFoods.map((food, idx) => (
                                                            <span key={idx} className="food-tag protein">{food}</span>
                                                        ))}
                                                    </div>
                                                </div>

                                                <div className="macro-section">
                                                    <div className="macro-header fats">
                                                        <span className="macro-icon">🥑</span>
                                                        <div className="macro-info">
                                                            <h4>Healthy Fats</h4>
                                                            <span className="macro-amount">{selectedReport.macronutrients.fats}g</span>
                                                            <span className="macro-percentage">({selectedReport.macronutrients.fatsPercentage})</span>
                                                        </div>
                                                    </div>
                                                    <div className="food-items">
                                                        {selectedReport.foodRecommendations.fatFoods.map((food, idx) => (
                                                            <span key={idx} className="food-tag fats">{food}</span>
                                                        ))}
                                                    </div>
                                                </div>

                                                <div className="macro-section">
                                                    <div className="macro-header carbs">
                                                        <span className="macro-icon">🌾</span>
                                                        <div className="macro-info">
                                                            <h4>Carbohydrates</h4>
                                                            <span className="macro-amount">{selectedReport.macronutrients.carbs}g</span>
                                                            <span className="macro-percentage">({selectedReport.macronutrients.carbsPercentage})</span>
                                                        </div>
                                                    </div>
                                                    <div className="food-items">
                                                        {selectedReport.foodRecommendations.carbFoods.map((food, idx) => (
                                                            <span key={idx} className="food-tag carbs">{food}</span>
                                                        ))}
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                    </section>

                                    <section className="detail-section">
                                        <h3>💧 Water Intake</h3>
                                        <div className="water-recommendation">
                                            <div className="water-stats">
                                                <div className="water-stat">
                                                    <span className="water-label">Current</span>
                                                    <span className="water-value">{selectedReport.waterRecommendation.currentIntake}L</span>
                                                </div>
                                                <div className="water-stat">
                                                    <span className="water-label">Recommended</span>
                                                    <span className="water-value">{selectedReport.waterRecommendation.recommendedIntake}L</span>
                                                </div>
                                                <div className={`water-status ${selectedReport.waterRecommendation.status.toLowerCase()}`}>
                                                    {selectedReport.waterRecommendation.status}
                                                </div>
                                            </div>
                                            <p>{selectedReport.waterRecommendation.advice}</p>
                                        </div>
                                    </section>

                                    <section className="detail-section">
                                        <h3>🏃 Activity Recommendations</h3>
                                        <ul className="recommendations-list">
                                            {selectedReport.activityRecommendations.map((rec, idx) => (
                                                <li key={idx}>{rec}</li>
                                            ))}
                                        </ul>
                                    </section>

                                    {selectedReport.allergies && selectedReport.allergies.length > 0 && (
                                        <section className="detail-section">
                                            <h3>⚠️ Dietary Restrictions</h3>
                                            <ul className="recommendations-list">
                                                {selectedReport.dietaryRestrictions.map((restriction, idx) => (
                                                    <li key={idx}>{restriction}</li>
                                                ))}
                                            </ul>
                                        </section>
                                    )}
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default History;


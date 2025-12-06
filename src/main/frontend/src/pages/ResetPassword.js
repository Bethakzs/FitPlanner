import React, { useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { authAPI } from '../services/api';

function ResetPassword() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [newPassword, setNewPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        
        if (!token) {
            setError('Invalid reset link');
            return;
        }
        
        try {
            await authAPI.resetPassword(token, newPassword);
            setMessage('Password successfully changed! Redirecting...');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Reset failed');
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-box">
                <h1>Reset Password</h1>
                {error && <div className="error">{error}</div>}
                {message && <div className="success">{message}</div>}
                <form onSubmit={handleSubmit}>
                    <input
                        type="password"
                        placeholder="New Password (min 8 characters)"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        minLength="8"
                    />
                    <button type="submit">Reset Password</button>
                </form>
                <div className="auth-links">
                    <Link to="/login">Back to Login</Link>
                </div>
            </div>
        </div>
    );
}

export default ResetPassword;


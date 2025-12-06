import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { authAPI } from '../services/api';

function ForgotPassword() {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        
        try {
            await authAPI.forgotPassword(email);
            setMessage('If the email exists, you will receive password reset instructions.');
        } catch (err) {
            setError(err.response?.data?.message || 'Request failed');
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-box">
                <h1>Forgot Password</h1>
                {error && <div className="error">{error}</div>}
                {message && <div className="success">{message}</div>}
                <form onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <button type="submit">Send Reset Link</button>
                </form>
                <div className="auth-links">
                    <Link to="/login">Back to Login</Link>
                </div>
            </div>
        </div>
    );
}

export default ForgotPassword;


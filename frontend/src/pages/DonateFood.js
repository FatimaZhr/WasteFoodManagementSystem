import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Added for redirection
import axios from 'axios'; // Added for API calls
import { Camera, MapPin, Clock, Utensils, Info } from 'lucide-react';
import './DonateFood.css';

const DonateFood = () => {
    const navigate = useNavigate();

    // State for the file upload
    const [selectedFile, setSelectedFile] = useState(null);

    const [formData, setFormData] = useState({
        title: '',
        category: 'Meals',
        quantity: '',
        expiryDate: '',
        address: '',
        description: ''
    });

    // Handle file selection
    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 1. Create FormData (Required for sending files)
        const data = new FormData();
        data.append("title", formData.title);
        data.append("category", formData.category);
        data.append("location", formData.address); // Maps "Address" to Backend "Location"
        data.append("expiryTime", formData.expiryDate);

        // Combine Quantity and Description into one field for the backend
        const fullDescription = `Quantity: ${formData.quantity}. ${formData.description}`;
        data.append("description", fullDescription);

        // Append the image if selected
        if (selectedFile) {
            data.append("image", selectedFile);
        }

        // 2. Send to Food Service (Port 8080)
        try {
            await axios.post('http://localhost:8080/api/bridge/food', data, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            alert("Thank you! Your donation has been listed.");
            navigate('/claim'); // Redirect to the food list page
        } catch (error) {
            console.error("Error submitting donation:", error);
            alert("Failed to save donation. Please try again.");
        }
    };

    return (
        <div className="donate-container">
            <div className="donate-card">
                <div className="donate-header">
                    <h1>Share Your Food</h1>
                    <p>Help reduce waste by sharing surplus food with others.</p>
                </div>

                <form onSubmit={handleSubmit} className="donate-form">
                    {/* Section: Basic Info */}
                    <div className="form-section">
                        <div className="input-group">
                            <label><Utensils size={18} /> Food Title</label>
                            <input
                                type="text"
                                placeholder="What are you donating?"
                                onChange={(e) => setFormData({...formData, title: e.target.value})}
                                required
                            />
                        </div>

                        <div className="input-row">
                            <div className="input-group">
                                <label>Category</label>
                                <select onChange={(e) => setFormData({...formData, category: e.target.value})}>
                                    <option value="Meals">Meals</option>
                                    <option value="Bakery">Bakery</option>
                                    <option value="Vegetables">Vegetables</option>
                                    <option value="Dairy">Dairy</option>
                                    <option value="Other">Other</option>
                                </select>
                            </div>
                            <div className="input-group">
                                <label>Quantity</label>
                                <input
                                    type="text"
                                    placeholder="e.g. 5 meals"
                                    onChange={(e) => setFormData({...formData, quantity: e.target.value})}
                                />
                            </div>
                        </div>
                    </div>

                    {/* Section: Logistics */}
                    <div className="form-section">
                        <div className="input-group">
                            <label><Clock size={18} /> Expiry Time</label>
                            <input
                                type="datetime-local"
                                onChange={(e) => setFormData({...formData, expiryDate: e.target.value})}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label><MapPin size={18} /> Pickup Location</label>
                            <input
                                type="text"
                                placeholder="Your address or pickup point"
                                onChange={(e) => setFormData({...formData, address: e.target.value})}
                                required
                            />
                        </div>
                    </div>

                    {/* Section: Description & Photo */}
                    <div className="form-section">
                        <div className="input-group">
                            <label><Info size={18} /> Additional Details</label>
                            <textarea
                                placeholder="Mention allergies, storage instructions, etc."
                                onChange={(e) => setFormData({...formData, description: e.target.value})}
                            ></textarea>
                        </div>

                        <div className="image-upload-box">
                            <Camera size={32} />
                            <span>Add a photo of the food</span>
                            {/* UPDATED: Added onChange handler here */}
                            <input
                                type="file"
                                className="file-input"
                                onChange={handleFileChange}
                                accept="image/*"
                            />
                        </div>
                    </div>

                    <button type="submit" className="donate-submit-btn">
                        List Donation Now
                    </button>
                </form>
            </div>
        </div>
    );
};

export default DonateFood;
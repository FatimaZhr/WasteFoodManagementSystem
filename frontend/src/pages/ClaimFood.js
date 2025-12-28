import React, { useState, useEffect } from 'react';
import axios from 'axios'; // Import axios to talk to Backend
import { MapPin, Clock, Search } from 'lucide-react';

const ClaimFood = () => {
    // 1. Change 'donations' from a static array to State
    const [foodItems, setFoodItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("All");

    // 2. Fetch real data from your Food Service (Port 8081)
    useEffect(() => {
        axios.get('http://localhost:8080/api/bridge/food')
            .then(response => {
                setFoodItems(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error fetching food:", error);
                setLoading(false);
            });
    }, []);

    // Helper to format the date nicely
    const formatDate = (dateString) => {
        if (!dateString) return "No expiry date";
        const date = new Date(dateString);
        return date.toLocaleDateString() + " " + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    // Function to handle the claim button click
        const handleClaim = async (id) => {
            if(!window.confirm("Are you sure you want to claim this item?")) return;

            try {
                // Send request to backend
                await axios.post(`http://localhost:8081/api/food/${id}/claim`);

                alert("Success! You have claimed this food.");

                // Remove the item from the screen immediately
                setFoodItems(foodItems.filter(item => item.id !== id));

            } catch (error) {
                console.error("Error claiming food:", error);
                alert("Failed to claim food. It might already be taken.");
            }
        };

        // Filter the items based on the Search Term
        const filteredItems = foodItems.filter((item) => {
          // 1. Check Search Text
          const matchesSearch = item.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                                item.location.toLowerCase().includes(searchTerm.toLowerCase());

          // 2. Check Category (If "All" is selected, everything matches)
          const matchesCategory = selectedCategory === "All" || item.category === selectedCategory;

          // 3. Return true only if BOTH match
          return matchesSearch && matchesCategory;
        });

    return (
        <div className="bg-gray-50 min-h-screen pb-20">
            {/* Search Header */}
            <div className="bg-white border-b py-8 px-6">
                <div className="max-w-6xl mx-auto">
                    <h1 className="text-3xl font-bold mb-6">Claim Fresh Food</h1>
                    <div className="flex flex-col md:flex-row gap-4">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-3 text-gray-400" size={20} />
<input
  type="text"
  placeholder="Search for food (e.g., snacks, vegetables)..."
  className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"

  // ADD THESE TWO LINES:
  value={searchTerm}
  onChange={(e) => setSearchTerm(e.target.value)}
/>                        </div>
                        <div className="flex gap-2">

                        <div className="flex gap-2 mb-6">
                          {["All", "Vegetables", "Bakery", "Cooked Meals"].map((category) => (
                            <button
                              key={category}
                              onClick={() => setSelectedCategory(category)}
                              className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                selectedCategory === category
                                  ? "bg-green-600 text-white"       // Active Style (Green)
                                  : "bg-gray-100 text-gray-600 hover:bg-gray-200" // Inactive Style (Gray)
                              }`}
                            >
                              {category}
                            </button>
                          ))}
                        </div>


                        </div>
                    </div>
                </div>
            </div>

            {/* Food Grid */}
            <div className="max-w-6xl mx-auto px-6 mt-12 grid grid-cols-1 md:grid-cols-3 gap-8">
                {loading ? (
                    <p className="text-gray-500 text-center col-span-3">Loading fresh food...</p>
                ) : foodItems.length === 0 ? (
                    <p className="text-gray-500 text-center col-span-3">No food available right now.</p>
                ) : (
                   filteredItems.map((item)=> (
                        <div key={item.id} className="bg-white rounded-3xl overflow-hidden shadow-sm hover:shadow-xl transition-all border border-gray-100 group">
                            <div className="relative h-48 overflow-hidden">
                                {/* Display the image from Backend */}
                                <img
                                    src={item.imageUrl}
                                    alt={item.title}
                                    className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                                    onError={(e) => {e.target.src = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500'}} // Fallback if image fails
                                />
                                <span className="absolute top-4 left-4 bg-white/90 backdrop-blur px-3 py-1 rounded-full text-xs font-bold text-green-700">
                                    {item.status || "Available Now"}
                                </span>
                            </div>
                            <div className="p-6">
                                <h3 className="text-xl font-bold mb-4">{item.title}</h3>
                                <div className="space-y-3 mb-6">
                                    <div className="flex items-center text-gray-500 text-sm gap-2">
                                        <Clock size={16} className="text-orange-500" />
                                        <span>Expires: <span className="font-medium text-gray-800">{formatDate(item.expirationTime)}</span></span>
                                    </div>
                                    <div className="flex items-center text-gray-500 text-sm gap-2">
                                        <MapPin size={16} className="text-green-500" />
                                        <span>{item.location}</span>
                                    </div>
                                    {/* Optional: Show description if you want */}
                                    {/* <p className="text-gray-400 text-sm mt-2">{item.description}</p> */}
                                </div>
 <button
    onClick={() => handleClaim(item.id)} // This connects the click to the function
    className="w-full bg-black text-white py-3 rounded-2xl font-bold hover:bg-gray-800 transition transform active:scale-95"
>
    Claim This Item
</button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default ClaimFood;
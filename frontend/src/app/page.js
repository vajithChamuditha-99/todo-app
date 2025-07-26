"use client";

import { useState, useEffect } from "react";

const API_BASE_URL = "http://localhost:8080/api/v1/tasks";

const Home = () => {
    const [taskTitle, setTaskTitle] = useState("");
    const [taskDescription, setTaskDescription] = useState("");
    const [tasks, setTasks] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize] = useState(5);
    const [totalElements, setTotalElements] = useState(0);
    const [activeTab, setActiveTab] = useState("new");
    const [isLoading, setIsLoading] = useState(false);
    const [showErrorModal, setShowErrorModal] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        setCurrentPage(0);
    }, [activeTab]);

    useEffect(() => {
        fetchTasks();
    }, [currentPage, activeTab]);

    const fetchTasks = async () => {
        setIsLoading(true);
        setErrorMessage("");
        setShowErrorModal(false);

        try {
            const isCompleted = activeTab === "new" ? false : true;
            const url = `${API_BASE_URL}?completed=${isCompleted}&page=${currentPage}&size=${pageSize}`;
            const response = await fetch(url);

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to fetch tasks.");
            }

            const data = await response.json();

            if (data.status === 0 && data.object && data.pagination) {
                setTasks(prevTasks =>
                    currentPage === 0 ? data.object : [...prevTasks, ...data.object]
                );
                setTotalElements(data.pagination.totalElements);
            } else {
                throw new Error(data.message || "Unexpected response structure from backend.");
            }
        } catch (error) {
            console.error("Error fetching tasks:", error);
            setErrorMessage(error.message || "An unexpected error occurred while fetching task list.");
            setShowErrorModal(true);
        } finally {
            setIsLoading(false);
        }
    };

    const handleAddTask = async () => {
        if (!taskTitle.trim() || !taskDescription.trim()) {
            setErrorMessage("Task title and description cannot be empty.");
            setShowErrorModal(true);
            return;
        }

        setIsLoading(true);
        setErrorMessage("");
        setShowErrorModal(false);

        try {
            const newTask = {
                title: taskTitle,
                description: taskDescription,
                completed: false,
            };

            const response = await fetch(API_BASE_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(newTask),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to add task.");
            }

            setTaskTitle("");
            setTaskDescription("");
            setTasks([]);
            setCurrentPage(0);
            fetchTasks();
        } catch (error) {
            console.error("Error adding task:", error);
            setErrorMessage(error.message || "An unexpected error occurred while adding the task.");
            setShowErrorModal(true);
        } finally {
            setIsLoading(false);
        }
    };

    const handleMarkDone = async (id) => {
        setIsLoading(true);
        setErrorMessage("");
        setShowErrorModal(false);

        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ completed: true }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to mark task as done.");
            }

            setTasks([]);
            setCurrentPage(0);
			fetchTasks();
        } catch (error) {
            console.error("Error marking task as done:", error);
            setErrorMessage(error.message || "An unexpected error occurred while marking the task as done.");
            setShowErrorModal(true);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSeeMore = () => {
        setCurrentPage(prevPage => prevPage + 1);
    };

    const displayedTasks = tasks;

    return (
        <div className="flex flex-col min-h-screen bg-gray-100 font-sans md:flex-row">
            {showErrorModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-xl max-w-sm w-full text-center">
                        <h3 className="text-lg font-bold text-red-600 mb-4">Error!</h3>
                        <p className="text-gray-700 mb-6">{errorMessage}</p>
                        <button
                            onClick={() => setShowErrorModal(false)}
                            className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}

            <div className="w-full p-6 md:w-1/2 md:p-10 lg:p-16 flex flex-col justify-center">
                <div className="bg-white p-8 rounded-xl shadow-lg">
                    <h2 className="text-3xl font-extrabold mb-6 text-blue-700 text-center">Add a New Task</h2>
                    <input
                        type="text"
                        placeholder="Task Title (e.g., Plan weekend trip)"
                        value={taskTitle}
                        onChange={(e) => setTaskTitle(e.target.value)}
                        className="w-full p-3 mb-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out"
                    />
                    <textarea
                        placeholder="Briefly describe the task details..."
                        value={taskDescription}
                        onChange={(e) => setTaskDescription(e.target.value)}
                        className="w-full p-3 mb-6 border border-gray-300 rounded-lg h-32 resize-y focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out"
                    />
                    <button
                        onClick={handleAddTask}
                        className="w-full bg-gradient-to-r from-blue-600 to-blue-800 text-white p-3 rounded-lg shadow-md hover:from-blue-700 hover:to-blue-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <div className="flex items-center justify-center">
                                <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></div>
                                Adding Task...
                            </div>
                        ) : (
                            "Add Task"
                        )}
                    </button>
                </div>
            </div>

            <div className="w-full p-6 md:w-1/2 md:p-10 lg:p-16 flex flex-col">
                <div className="bg-white p-8 rounded-xl shadow-lg flex-grow">
                    <h2 className="text-3xl font-extrabold mb-6 text-blue-700 text-center">Your Tasks</h2>

                    <div className="w-full mb-6 grid grid-cols-2 gap-2">
                        <button
                            onClick={() => setActiveTab("new")}
                            className={`p-3 rounded-l-lg font-semibold text-lg transition duration-300 ease-in-out ${
                                activeTab === "new"
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                            }`}
                            disabled={isLoading}
                        >
                            New
                        </button>
                        <button
                            onClick={() => setActiveTab("completed")}
                            className={`p-3 rounded-r-lg font-semibold text-lg transition duration-300 ease-in-out ${
                                activeTab === "completed"
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                            }`}
                            disabled={isLoading}
                        >
                            Completed
                        </button>
                    </div>

                    {isLoading && (
                        <div className="flex justify-center items-center h-48">
                            <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-600"></div>
                        </div>
                    )}

                    {!isLoading && displayedTasks.length > 0 ? (
                        <>
                            {displayedTasks.map((task) => (
                                <div
                                    key={task.id}
                                    className="bg-gray-50 p-5 mb-4 rounded-lg shadow-sm border border-gray-200 flex flex-col md:flex-row justify-between items-start md:items-center transition duration-200 ease-in-out transform hover:scale-[1.01]"
                                >
                                    <div className="flex-grow mb-3 md:mb-0">
                                        <h3 className={`font-bold text-xl ${task.completed ? "line-through text-gray-500" : "text-gray-800"}`}>
                                            {task.title}
                                        </h3>
                                        <p className={`text-gray-600 mt-1 ${task.completed ? "line-through" : ""}`}>
                                            {task.description}
                                        </p>
                                        <div className="text-sm text-gray-500 mt-2">
                                            Created: {new Date(task.createdAt).toLocaleString("en-US", {
                                                month: "short",
                                                day: "2-digit",
                                                year: "numeric",
                                                hour: "2-digit",
                                                minute: "2-digit",
                                                hour12: true,
                                                timeZoneName: "short",
                                            })}
                                        </div>
                                    </div>
                                    {activeTab === "new" && (
                                        <button
                                            onClick={() => handleMarkDone(task.id)}
                                            className="mt-4 md:mt-0 bg-green-600 text-white px-5 py-2 rounded-full shadow-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105"
                                            disabled={isLoading}
                                        >
                                            Mark Done
                                        </button>
                                    )}
                                </div>
                            ))}

                            {displayedTasks.length < totalElements && (
                                <button
                                    onClick={handleSeeMore}
                                    className="w-full bg-gradient-to-r from-blue-500 to-blue-700 text-white p-3 rounded-lg shadow-md hover:from-blue-600 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105 mt-6"
                                    disabled={isLoading}
                                >
                                    {isLoading ? (
                                        <div className="flex items-center justify-center">
                                            <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></div>
                                            Loading More...
                                        </div>
                                    ) : (
                                        "See More"
                                    )}
                                </button>
                            )}
                        </>
                    ) : (
                        !isLoading && <p className="text-center text-gray-500 text-lg mt-10">No tasks available in this category.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Home;
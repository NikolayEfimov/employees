import React, { useState, useEffect } from 'react';
import { format } from 'date-fns';
import api from './api';
import { Employee } from './types';
import EmployeeForm from './EmployeeForm';
import styles from './EmployeeList.module.css';

const EmployeeList: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
    const [supervisorId, setSupervisorId] = useState<string>('');
    const [subordinateIds, setSubordinateIds] = useState<string>('');
    const [showSupervisorForm, setShowSupervisorForm] = useState<boolean>(false);
    const [showSubordinateForm, setShowSubordinateForm] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchEmployees();
    }, []);

    const fetchEmployees = async () => {
        try {
            const response = await api.get<Employee[]>('/employees');
            if (Array.isArray(response.data)) {
                setEmployees(response.data);
            } else {
                setEmployees([]);
                console.error('API response is not an array:', response.data);
            }
        } catch (error) {
            console.error('Failed to fetch employees:', error);
            setEmployees([]);
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await api.delete(`/employees/${id}`);
            fetchEmployees();
            setError(null); // Clear any previous errors
        } catch (err: any) {
            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError('An error occurred. Please try again.');
            }
        }
    };

    const handleEdit = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id) {
            setSelectedEmployee(null);
        } else {
            setSelectedEmployee(employee);
        }
        setError(null);
    };

    const handleFormSubmit = () => {
        fetchEmployees();
        setSelectedEmployee(null);
        setError(null);
    };

    const handleCancelEdit = () => {
        setSelectedEmployee(null);
        setError(null);
    };

    const handleSupervisorChange = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id && showSupervisorForm) {
            setShowSupervisorForm(false);
            setSelectedEmployee(null);
        } else {
            setSelectedEmployee(employee);
            setShowSupervisorForm(true);
        }
        setError(null);
    };

    const handleSubordinateChange = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id && showSubordinateForm) {
            setShowSubordinateForm(false);
            setSelectedEmployee(null);
        } else {
            setSelectedEmployee(employee);
            setShowSubordinateForm(true);
        }
        setError(null);
    };

    const handleSupervisorSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        if (selectedEmployee) {
            try {
                await api.patch(`/employees/${selectedEmployee.id}`, { supervisorId });
                fetchEmployees();
                setSelectedEmployee(null);
                setSupervisorId('');
                setShowSupervisorForm(false);
            } catch (err: any) {
                if (err.response && err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                } else {
                    setError('An error occurred. Please try again.');
                }
            }
        }
    };

    const handleSubordinateSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        if (selectedEmployee) {
            try {
                const idsArray = subordinateIds.split(',').map(id => id.trim());
                await api.post(`/employees/${selectedEmployee.id}/add-subordinates`, idsArray);
                fetchEmployees();
                setSelectedEmployee(null);
                setSubordinateIds('');
                setShowSubordinateForm(false);
            } catch (err: any) {
                if (err.response && err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                } else {
                    setError('An error occurred. Please try again.');
                }
            }
        }
    };

    const getSupervisorButtonText = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id && showSupervisorForm) {
            return "Cancel";
        } else if (employee.supervisor) {
            return "Change Supervisor";
        } else {
            return "Assign Supervisor";
        }
    };

    const getSubordinateButtonText = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id && showSubordinateForm) {
            return "Cancel";
        } else {
            return "Assign Subordinates";
        }
    };

    const isValidDate = (date: any) => {
        return !isNaN(Date.parse(date));
    };

    return (
        <div className={styles.container}>
            <h1>Employee List</h1>
            <EmployeeForm
                employee={selectedEmployee}
                onFormSubmit={handleFormSubmit}
                onCancelEdit={handleCancelEdit}
            />
            {error && <div className={styles.error}>{error}</div>}
            <ul>
                {employees.map((employee) => (
                    <li key={employee.id}>
                        <div className={styles.employeeInfo}>
                            <p><strong>ID:</strong> {employee.id}</p>
                            <p><strong>First Name:</strong> {employee.firstName}</p>
                            <p><strong>Last Name:</strong> {employee.lastName}</p>
                            <p><strong>Position:</strong> {employee.position}</p>
                            <p><strong>Creation Date:</strong>
                                {isValidDate(employee.creationDate)
                                    ? format(new Date(employee.creationDate), 'MMMM dd, yyyy HH:mm')
                                    : 'N/A'}
                            </p>
                            {employee.supervisor && (
                                <p><strong>Supervisor:</strong> {employee.supervisor.firstName} {employee.supervisor.lastName}</p>
                            )}
                        </div>
                        <div className={styles.buttonGroup}>
                            <button onClick={() => handleEdit(employee)}>
                                {selectedEmployee?.id === employee.id ? "Cancel Edit" : "Edit"}
                            </button>
                            <button onClick={() => handleDelete(employee.id)}>Delete</button>
                            <button onClick={() => handleSupervisorChange(employee)}>
                                {getSupervisorButtonText(employee)}
                            </button>
                            <button onClick={() => handleSubordinateChange(employee)}>
                                {getSubordinateButtonText(employee)}
                            </button>
                        </div>
                    </li>
                ))}
            </ul>

            {showSupervisorForm && selectedEmployee && (
                <div className={styles.supervisorFormContainer}>
                    <h3>Change Supervisor for {selectedEmployee.firstName} {selectedEmployee.lastName}</h3>
                    {error && <div className={styles.error}>{error}</div>}
                    <form onSubmit={handleSupervisorSubmit}>
                        <label>
                            Supervisor ID:
                            <input
                                type="text"
                                value={supervisorId}
                                onChange={(e) => {
                                    setSupervisorId(e.target.value);
                                    setError(null);
                                }}
                            />
                        </label>
                        <button type="submit">Submit</button>
                    </form>
                </div>
            )}

            {showSubordinateForm && selectedEmployee && (
                <div className={styles.supervisorFormContainer}>
                    <h3>Assign Subordinates to {selectedEmployee.firstName} {selectedEmployee.lastName}</h3>
                    {error && <div className={styles.error}>{error}</div>}
                    <form onSubmit={handleSubordinateSubmit}>
                        <label>
                            Subordinate IDs (comma-separated):
                            <input
                                type="text"
                                value={subordinateIds}
                                onChange={(e) => {
                                    setSubordinateIds(e.target.value);
                                    setError(null);
                                }}
                            />
                        </label>
                        <button type="submit">Submit</button>
                    </form>
                </div>
            )}
        </div>
    );
};

export default EmployeeList;

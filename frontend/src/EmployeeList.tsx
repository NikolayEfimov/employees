import React, { useState, useEffect } from 'react';
import api from './api';
import { Employee } from './types';
import EmployeeForm from './EmployeeForm';
import styles from './EmployeeList.module.css';

const EmployeeList: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
    const [supervisorId, setSupervisorId] = useState<string>('');
    const [showSupervisorForm, setShowSupervisorForm] = useState<boolean>(false);

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
        } catch (error) {
            console.error('Failed to delete employee:', error);
        }
    };

    const handleEdit = (employee: Employee) => {
        setSelectedEmployee(employee);
    };

    const handleFormSubmit = () => {
        fetchEmployees();
        setSelectedEmployee(null);
    };

    const handleSupervisorChange = (employee: Employee) => {
        if (selectedEmployee?.id === employee.id && showSupervisorForm) {
            setShowSupervisorForm(false);
            setSelectedEmployee(null);
        } else {
            setSelectedEmployee(employee);
            setShowSupervisorForm(true);
        }
    };

    const handleSupervisorSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (selectedEmployee) {
            try {
                await api.patch(`/employees/${selectedEmployee.id}`, { supervisorId });
                fetchEmployees();
                setSelectedEmployee(null);
                setSupervisorId('');
                setShowSupervisorForm(false);
            } catch (error) {
                console.error('Failed to update supervisor:', error);
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

    return (
        <div className={styles.container}>
            <h1>Employee List</h1>
            <EmployeeForm
                employee={selectedEmployee}
                onFormSubmit={handleFormSubmit}
            />
            <ul>
                {employees.map((employee) => (
                    <li key={employee.id}>
                        <div className={styles.employeeInfo}>
                            {employee.firstName} {employee.lastName} - {employee.position}
                            {employee.supervisor && (
                                <div className={styles.supervisorInfo}>
                                    Supervisor: {employee.supervisor.firstName} {employee.supervisor.lastName}
                                </div>
                            )}
                        </div>
                        <div className={styles.buttonGroup}>
                            <button onClick={() => handleEdit(employee)}>Edit</button>
                            <button onClick={() => handleDelete(employee.id)}>Delete</button>
                            <button onClick={() => handleSupervisorChange(employee)}>
                                {getSupervisorButtonText(employee)}
                            </button>
                        </div>
                    </li>
                ))}
            </ul>

            {showSupervisorForm && selectedEmployee && (
                <div className={styles.supervisorFormContainer}>
                    <h3>Change Supervisor for {selectedEmployee.firstName} {selectedEmployee.lastName}</h3>
                    <form onSubmit={handleSupervisorSubmit}>
                        <label>
                            Supervisor ID:
                            <input
                                type="text"
                                value={supervisorId}
                                onChange={(e) => setSupervisorId(e.target.value)}
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

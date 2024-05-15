import React, { useState, useEffect } from 'react';
import api from './api';
import { Employee } from './types';
import EmployeeForm from './EmployeeForm';
import styles from './EmployeeList.module.css';

const EmployeeList: React.FC = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);

    useEffect(() => {
        fetchEmployees();
    }, []);

    const fetchEmployees = async () => {
        const response = await api.get<Employee[]>('/employees');
        setEmployees(response.data);
    };

    const handleDelete = async (id: number) => {
        await api.delete(`/employees/${id}`);
        fetchEmployees();
    };

    const handleEdit = (employee: Employee) => {
        setSelectedEmployee(employee);
    };

    const handleFormSubmit = () => {
        fetchEmployees();
        setSelectedEmployee(null);
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
                        </div>
                        <div className={styles.buttonGroup}>
                            <button onClick={() => handleEdit(employee)}>Edit</button>
                            <button onClick={() => handleDelete(employee.id)}>Delete</button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default EmployeeList;

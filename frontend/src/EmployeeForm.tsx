import React, { useState, useEffect } from 'react';
import api from './api';
import { Employee } from './types';
import styles from './EmployeeForm.module.css';

interface EmployeeFormProps {
    employee: Employee | null;
    onFormSubmit: () => void;
}

const EmployeeForm: React.FC<EmployeeFormProps> = ({ employee, onFormSubmit }) => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        position: '',
        supervisorId: '',
    });
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (employee) {
            setFormData({
                firstName: employee.firstName,
                lastName: employee.lastName,
                position: employee.position,
                supervisorId: employee.supervisor ? employee.supervisor.id.toString() : '',
            });
        }
    }, [employee]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        try {
            if (employee) {
                await api.patch(`/employees/${employee.id}`, formData);
            } else {
                await api.post('/employees', formData);
            }
            onFormSubmit();
            setFormData({
                firstName: '',
                lastName: '',
                position: '',
                supervisorId: '',
            });
        } catch (err: any) {
            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError('An error occurred. Please try again.');
            }
        }
    };

    return (
        <div className={styles.formContainer}>
            <form onSubmit={handleSubmit}>
                {error && <div className={styles.error}>{error}</div>}
                <div className={styles.formGroup}>
                    <label>First Name:</label>
                    <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                    />
                </div>
                <div className={styles.formGroup}>
                    <label>Last Name:</label>
                    <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                    />
                </div>
                <div className={styles.formGroup}>
                    <label>Position:</label>
                    <input
                        type="text"
                        name="position"
                        value={formData.position}
                        onChange={handleChange}
                    />
                </div>
                <div className={styles.formGroup}>
                    <label>Supervisor ID:</label>
                    <input
                        type="text"
                        name="supervisorId"
                        value={formData.supervisorId}
                        onChange={handleChange}
                    />
                </div>
                <button type="submit">{employee ? 'Update' : 'Create'} Employee</button>
            </form>
        </div>
    );
};

export default EmployeeForm;

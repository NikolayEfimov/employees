import React, { useState, useEffect } from 'react';
import api from './api';
import { Employee } from './types';
import styles from './EmployeeForm.module.css';

interface EmployeeFormProps {
    employee: Employee | null;
    onFormSubmit: () => void;
    onCancelEdit: () => void;
}

const EmployeeForm: React.FC<EmployeeFormProps> = ({ employee, onFormSubmit, onCancelEdit }) => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        position: '',
        supervisorId: '',
    });
    const [errors, setErrors] = useState<Record<string, string>>({});

    useEffect(() => {
        if (employee) {
            setFormData({
                firstName: employee.firstName,
                lastName: employee.lastName,
                position: employee.position,
                supervisorId: employee.supervisor ? employee.supervisor.id.toString() : '',
            });
        } else {
            setFormData({
                firstName: '',
                lastName: '',
                position: '',
                supervisorId: '',
            });
        }
    }, [employee]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
        setErrors((prevErrors) => ({
            ...prevErrors,
            [name]: '',
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
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
            setErrors({});
        } catch (err: any) {
            if (err.response && err.response.data && err.response.data.messages) {
                setErrors(err.response.data.messages);
            } else {
                setErrors({ form: 'An error occurred. Please try again.' });
            }
        }
    };

    return (
        <div className={styles.formContainer}>
            <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <label>First Name:</label>
                    <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                    />
                    {errors.firstName && <div className={styles.error}>{errors.firstName}</div>}
                </div>
                <div className={styles.formGroup}>
                    <label>Last Name:</label>
                    <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                    />
                    {errors.lastName && <div className={styles.error}>{errors.lastName}</div>}
                </div>
                <div className={styles.formGroup}>
                    <label>Position:</label>
                    <input
                        type="text"
                        name="position"
                        value={formData.position}
                        onChange={handleChange}
                    />
                    {errors.position && <div className={styles.error}>{errors.position}</div>}
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
                {errors.form && <div className={styles.error}>{errors.form}</div>}
                <button type="button" onClick={onCancelEdit}>Cancel</button>
            </form>
        </div>
    );
};

export default EmployeeForm;

export interface Employee {
    id: number;
    firstName: string;
    lastName: string;
    position: string;
    creationDate: string;
    supervisor?: Employee;
}
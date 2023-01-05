import { Room } from './room';

export interface Rent {
    id: number;
    beginTime: Date;
    endTime: Date;
    board: boolean;
    finalCost: number;
    room: Room;
}

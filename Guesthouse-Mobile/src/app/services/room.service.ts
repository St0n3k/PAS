import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Room } from '../model/room';
import { Rent } from '../model/rent';

@Injectable({
    providedIn: 'root'
})
export class RoomService {
    constructor(private http: HttpClient) {}

    getRooms() {
        return this.http.get<Room[]>(`${environment.apiUrl}/rooms`, {
            observe: 'response'
        });
    }

    getRoomById(id: number) {
        return this.http.get<Room>(`${environment.apiUrl}/rooms/${id}`, {
            observe: 'response'
        });
    }

    rentRoomForSelf(id: number, body: object) {
        return this.http.post<Rent>(`${environment.apiUrl}/rooms/${id}`, body, {
            observe: 'response'
        });
    }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Room } from '../model/room';
import { Observable } from 'rxjs';

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
}

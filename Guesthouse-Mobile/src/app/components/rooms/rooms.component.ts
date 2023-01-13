import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../model/room';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-rooms',
    templateUrl: './rooms.component.html',
    styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit {
    rooms: Room[] | null | undefined;

    constructor(private roomService: RoomService) {}

    ngOnInit(): void {
        this.roomService.getRooms().subscribe((result) => {
            this.rooms = result.body;
        });
    }

    getRooms() {
        return this.rooms;
    }
}

import { Component, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../model/room';
import { ActivatedRoute, Router } from '@angular/router';
import { PagenotfoundComponent } from '../pagenotfound/pagenotfound.component';

//TODO kalendarz z zajętymi terminami z rentów
//TODO przycisk do wynajęcia i modal z wyborem daty

@Component({
    selector: 'app-room-details',
    templateUrl: './room-details.component.html',
    styleUrls: ['./room-details.component.css']
})
export class RoomDetailsComponent implements OnInit {
    roomID: number | undefined;
    room: Room | null | undefined;
    constructor(
        private roomService: RoomService,
        private route: ActivatedRoute,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe((paramMap) => {
            this.roomID = parseInt(<string>paramMap.get('roomID'));
        });
        if (this.roomID != undefined) {
            this.roomService.getRoomById(this.roomID).subscribe((result) => {
                this.room = result.body;
            });
        }
    }
}

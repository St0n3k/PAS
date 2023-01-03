import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { Room } from '../../model/room';
import { ActivatedRoute, Router } from '@angular/router';
import {
    NgbDate,
    NgbDateAdapter,
    NgbDateNativeUTCAdapter,
    NgbModal,
    NgbModalRef
} from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { formatDate } from '@angular/common';

//TODO kalendarz z zajętymi terminami z rentów
//TODO przycisk do wynajęcia i modal z wyborem daty

@Component({
    selector: 'app-room-details',
    templateUrl: './room-details.component.html',
    styleUrls: ['./room-details.component.css'],
    providers: [{ provide: NgbDateAdapter, useClass: NgbDateNativeUTCAdapter }]
})
export class RoomDetailsComponent implements OnInit {
    rentForSelfForm = new FormGroup({
        beginDate: new FormControl(new Date(), [Validators.required]),
        endDate: new FormControl(new Date(), [Validators.required]),
        board: new FormControl('')
    });
    today: NgbDate;
    roomID: number | undefined;
    room: Room | null | undefined;
    rentModal: NgbModalRef | undefined;

    constructor(
        private roomService: RoomService,
        private route: ActivatedRoute,
        private router: Router,
        private modalService: NgbModal,
        @Inject(LOCALE_ID) public locale: string
    ) {
        let today = new Date();
        this.today = new NgbDate(
            today.getFullYear(),
            today.getMonth() + 1,
            today.getDay() + 1
        );
    }

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

    showRentModal(rentModal: any): void {
        this.rentModal = this.modalService.open(rentModal, {
            size: 'xl',
            centered: true,
            scrollable: true
        });
    }

    showConfirmRentModal(confirmRentModal: any): void {
        this.modalService.open(confirmRentModal, {
            centered: true,
            scrollable: true
        });
    }

    rentRoomForSelf() {
        if (this.rentForSelfForm.valid) {
            let board = this.rentForSelfForm.getRawValue().board !== '';
            let beginTime = formatDate(
                this.rentForSelfForm.getRawValue().beginDate!,
                'yyyy-dd-MMTHH:MM:SS',
                this.locale
            );
            let endTime = formatDate(
                this.rentForSelfForm.getRawValue().endDate!,
                'yyyy-dd-MMTHH:MM:SS',
                this.locale
            );
            console.log({ board, beginDate: beginTime, endDate: endTime });
            this.roomService
                .rentRoomForSelf(this.roomID!, {
                    board,
                    beginTime,
                    endTime
                })
                .subscribe(
                    (result) => {
                        if (result.status == 201) {
                            console.log('SUCCESS');
                            this.rentModal?.close();
                        }
                    },
                    (error) => {
                        console.log('FAILURE');
                        //TODO handling errorów
                    }
                );
        }
    }
}

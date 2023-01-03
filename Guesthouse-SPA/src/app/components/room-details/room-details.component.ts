import {
    Component,
    Inject,
    LOCALE_ID,
    OnInit,
    TemplateRef,
    ViewChild
} from '@angular/core';
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
import { AuthService } from '../../services/auth.service';

//TODO kalendarz z zajętymi terminami z rentów

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
    @ViewChild('rentErrorModal') rentErrorModal: TemplateRef<any> | undefined;

    status = 0;
    // 0 - ok
    // 1 - conflict
    // 2 - unathorized
    // 3 - End date is before begin date
    // 4 - anything else

    constructor(
        private roomService: RoomService,
        private authService: AuthService,
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

    getRole() {
        return this.authService.getRole();
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

    showRentErrorModal(): void {
        this.modalService.open(this.rentErrorModal, {
            centered: true,
            scrollable: true
        });
    }

    rentRoomForSelf() {
        if (this.rentForSelfForm.valid) {
            let board = this.rentForSelfForm.getRawValue().board !== '';
            let beginTime = formatDate(
                this.rentForSelfForm.getRawValue().beginDate!,
                'yyyy-MM-ddTHH:MM:SS',
                this.locale
            );
            let endTime = formatDate(
                this.rentForSelfForm.getRawValue().endDate!,
                'yyyy-MM-ddTHH:MM:SS',
                this.locale
            );

            if (
                this.rentForSelfForm.getRawValue().endDate! <
                this.rentForSelfForm.getRawValue().beginDate!
            ) {
                this.status = 3;
                this.showRentErrorModal();
                return;
            }

            this.roomService
                .rentRoomForSelf(this.roomID!, {
                    board,
                    beginTime,
                    endTime
                })
                .subscribe(
                    (result) => {
                        if (result.status == 201) {
                            this.status = 0;
                            this.rentModal?.close();
                        }
                    },
                    (error) => {
                        if (error.status == 409) {
                            this.status = 1;
                        } else if (error.status == 401) {
                            this.status = 2;
                        } else if (error.status == 400) {
                            this.status = 3;
                        } else {
                            this.status = 4;
                        }
                        this.showRentErrorModal();
                    }
                );
        }
    }
}

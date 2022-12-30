import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html',
    styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
    changePasswordForm = new FormGroup({
        oldPassword: new FormControl('', [Validators.required]), //Validators.email]),
        newPassword: new FormControl('', [Validators.required]),
        newPasswordConfirm: new FormControl('', [Validators.required])
    });

    constructor(private authService: AuthService, private router: Router) {}

    ngOnInit(): void {}

    get newPasswordConfirm() {
        return this.changePasswordForm.get('newPasswordConfirm');
    }

    get newPassword() {
        return this.changePasswordForm.get('newPassword');
    }

    onSubmit() {
        if (this.changePasswordForm.valid) {
            let oldPassword = this.changePasswordForm.getRawValue().oldPassword;
            let newPassword = this.changePasswordForm.getRawValue().newPassword;
            let newPasswordConfirm =
                this.changePasswordForm.getRawValue().newPasswordConfirm;

            if (newPassword != newPasswordConfirm) {
                this.newPasswordConfirm?.setErrors({ notSame: true });
                return;
            }
            if (oldPassword === newPassword) {
                this.newPassword?.setErrors({ sameAsOld: true });
                return;
            }

            this.authService
                .changePassword(
                    oldPassword!.toString(),
                    newPassword!.toString()
                )
                .subscribe(
                    (result) => {
                        if (result.status == 200) {
                            this.clearInputs();
                        }
                    },
                    (error) => {
                        //TODO komunikat błędu
                    }
                );
        }
    }

    clearInputs() {
        this.changePasswordForm.get('oldPassword')?.reset();
        this.changePasswordForm.get('newPassword')?.reset();
        this.changePasswordForm.get('newPasswordConfirm')?.reset();
    }
}

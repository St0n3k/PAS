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
        newPassword: new FormControl('', [
            Validators.required,
            Validators.minLength(8),
            Validators.maxLength(60)
        ]),
        newPasswordConfirm: new FormControl('', [
            Validators.required,
            Validators.minLength(8),
            Validators.maxLength(60)
        ])
    });
    fail = 0;
    success = 0;

    constructor(private authService: AuthService, private router: Router) {}

    ngOnInit(): void {}

    get oldPassword() {
        return this.changePasswordForm.get('oldPassword');
    }

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
                            this.fail = 0;
                            this.success = 1;
                            this.clearInputs();
                        }
                    },
                    (error) => {
                        this.fail = 1;
                        this.success = 0;
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

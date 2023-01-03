import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    loginForm = new FormGroup({
        email: new FormControl('', [Validators.required]), //Validators.email]),
        password: new FormControl('', [Validators.required])
    });
    fail = 0;

    constructor(private authService: AuthService, private router: Router) {}

    ngOnInit(): void {}

    get passwordInput() {
        return this.loginForm.get('password');
    }

    onSubmit() {
        if (this.loginForm.valid) {
            let email = this.loginForm.getRawValue().email;
            let password = this.loginForm.getRawValue().password;

            this.authService
                .login(email!.toString(), password!.toString())
                .subscribe(
                    (result) => {
                        if (result.status == 200) {
                            this.authService.saveUserData(result);
                            this.authService.authenticated.next(true);
                            this.router.navigate(['/']);
                        }
                    },
                    (error) => {
                        this.fail = 1;
                        this.authService.clearUserData();
                        this.authService.authenticated.next(false);
                        this.clearPassword();
                    }
                );
        }
    }

    clearPassword() {
        this.loginForm.get('password')?.reset();
    }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginResponse } from '../model/loginResponse';
import { environment } from '../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import jwt_decode from 'jwt-decode';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    authenticated = new BehaviorSubject(false);

    constructor(private http: HttpClient, private router: Router) {
        if (localStorage.getItem('email') != null) {
            this.authenticated.next(true);
        }

        window.addEventListener(
            'storage',
            (event) => {
                if (event.storageArea == localStorage) {
                    let token = localStorage.getItem('jwt');
                    if (token == undefined) {
                        window.location.href = '/login';
                    }
                }
            },
            false
        );
    }

    login(username: string, password: string) {
        return this.http.post<LoginResponse>(
            `${environment.apiUrl}/login`,
            { username, password },
            { observe: 'response' }
        );
    }

    saveUserData(result: any) {
        const tokenInfo = this.getDecodedJwtToken(result.body.jwt);
        localStorage.setItem('email', result.body.email);
        localStorage.setItem('jwt', result.body.jwt);
        localStorage.setItem('role', tokenInfo.roles[0]);
    }

    register(email: string, username: string, password: string) {
        return this.http.post(
            `${environment.apiUrl}/register`,
            { email, username, password },
            { observe: 'response' }
        );
    }

    changePassword(oldPassword: string, newPassword: string) {
        return this.http.put<LoginResponse>(
            `${environment.apiUrl}/changePassword`,
            { oldPassword, newPassword },
            { observe: 'response' }
        );
    }

    logout() {
        this.clearUserData();
        this.authenticated.next(false);
        this.router.navigate(['/login'], {
            queryParams: { 'logout-success': true }
        });
    }

    getRole() {
        return localStorage.getItem('role');
    }

    getEmail() {
        return localStorage.getItem('email');
    }

    clearUserData() {
        localStorage.removeItem('role');
        localStorage.removeItem('email');
        localStorage.removeItem('jwt');
    }

    getDecodedJwtToken(token: string): any {
        try {
            return jwt_decode(token);
        } catch (Error) {
            return null;
        }
    }
}

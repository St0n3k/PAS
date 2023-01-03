import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AdminGuard } from './admin.guard';

@Injectable({
    providedIn: 'root'
})
export class EmployeeGuard implements CanActivate {
    constructor(
        private router: Router,
        private authService: AuthService,
        private adminGuard: AdminGuard
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ):
        | Observable<boolean | UrlTree>
        | Promise<boolean | UrlTree>
        | boolean
        | UrlTree {
        if (this.adminGuard.canActivate(route, state)) {
            console.log('ADMIN');
            return true;
        }
        if (this.authService.getRole() != 'EMPLOYEE') {
            this.router.navigate(['/']);
            return false;
        }
        return true;
    }
}

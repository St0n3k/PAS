import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoomsComponent } from './components/rooms/rooms.component';
import { PagenotfoundComponent } from './components/pagenotfound/pagenotfound.component';
import { ClientsComponent } from './components/clients/clients.component';
import { EmployeesComponent } from './components/employees/employees.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { EmployeeGuard } from './guards/employee.guard';
import { AdminGuard } from './guards/admin.guard';
import { LoginComponent } from './components/login/login.component';
import { LoginRegisterGuard } from './guards/login-register.guard';
import { AuthGuard } from './guards/auth.guard';
import { ChangePasswordComponent } from './components/change-password/change-password.component';

const routes: Routes = [
    { path: '', component: HomepageComponent },
    {
        path: 'login',
        component: LoginComponent,
        canActivate: [LoginRegisterGuard]
    },
    {
        path: 'changePassword',
        component: ChangePasswordComponent,
        canActivate: [AuthGuard]
    },
    { path: 'rooms', component: RoomsComponent },
    {
        path: 'clients',
        component: ClientsComponent,
        canActivate: [AdminGuard, EmployeeGuard]
    },
    {
        path: 'employees',
        component: EmployeesComponent,
        canActivate: [AdminGuard]
    },
    { path: '**', component: PagenotfoundComponent }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}

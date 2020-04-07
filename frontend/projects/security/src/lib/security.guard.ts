import {Injectable} from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  CanLoad,
  Route,
  UrlSegment
} from '@angular/router';
import {Observable, of} from 'rxjs';
import {SecurityService} from 'projects/security/src/lib/security.service';
import {flatMap, map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SecurityGuard implements CanActivate, CanLoad {
  constructor(private security: SecurityService) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.can();
  }

  canLoad(route: Route, segments: UrlSegment[]): Observable<boolean> | Promise<boolean> | boolean {
    return this.can();
  }

  private can(): Observable<boolean> {
    return this.security.init().pipe(
      flatMap(authenticated => {
        if (!authenticated) {
          return this.security.login().pipe(map(() => false));
        }
        return of(true);
      })
    );
  }
}

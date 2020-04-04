import {TestBed} from '@angular/core/testing';

import {SecurityGuard} from './security.guard';
import SpyObj = jasmine.SpyObj;
import {SecurityService} from 'projects/security/src/lib/security.service';
import {securityServiceSpy} from 'projects/security/src/lib/security.service.spec';
import {Observable, of} from 'rxjs';

describe('SecurityGuard', () => {
  let guard: SecurityGuard;
  let service: SpyObj<SecurityService>;

  beforeEach(() => {
    service = securityServiceSpy();
    TestBed.configureTestingModule({
      providers: [
        {provide: SecurityService, useValue: service}
      ]
    });
    guard = TestBed.inject(SecurityGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should canActivate redirect login', () => {
    service.init.and.returnValue(of(false));
    service.login.and.returnValue(of(null));

    const response: Observable<boolean> = guard.canActivate(null, null) as any;
    response.subscribe(value => expect(value).toBeFalse());
  });

  it('should canActivate continue', () => {
    service.init.and.returnValue(of(true));
    const response: Observable<boolean> = guard.canActivate(null, null) as any;
    response.subscribe(value => expect(value).toBeTrue());
  });
});

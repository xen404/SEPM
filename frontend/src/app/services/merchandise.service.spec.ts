import { TestBed } from '@angular/core/testing';

import { MerchandiseService } from './merchandise.service';

describe('MerchandiseService', () => {
  let service: MerchandiseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MerchandiseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

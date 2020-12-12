import { TestBed } from '@angular/core/testing';

import { TopEventService } from './top-event.service';

describe('TopEventService', () => {
  let service: TopEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TopEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

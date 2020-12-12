import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowSeatsComponent } from './show-seats.component';

describe('ShowSeatsComponent', () => {
  let component: ShowSeatsComponent;
  let fixture: ComponentFixture<ShowSeatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowSeatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowSeatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

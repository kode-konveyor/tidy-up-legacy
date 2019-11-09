package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class WorkRequestTest extends TestsBase {
	protected final TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	protected final WorkRequestRepository workRequestRepository = Mockito.mock(WorkRequestRepository.class);

	protected WorkRequestController workRequestController;

	@BeforeEach
	public void setUp() {
		this.workRequestController = new WorkRequestController(tidyUserRepository, workRequestRepository);
	}
}

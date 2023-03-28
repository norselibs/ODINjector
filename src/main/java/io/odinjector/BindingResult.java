package io.odinjector;

public class BindingResult<T> {
	Binding<T> binding = null;
	BindingContext context = null;

	private BindingResult() {

	}

	public boolean isEmpty() {
		return binding == null;
	}

	public static <C> BindingResult<C> of(Binding<C> binding, BindingContext context) {
		BindingResult result = new BindingResult();
		result.binding = binding;
		result.context = context;
		return result;
	}

	public static <T> BindingResult<T> empty() {
		return new BindingResult<>();
	}

	public boolean isInterface() {
		return binding.isInterface();
	}
}

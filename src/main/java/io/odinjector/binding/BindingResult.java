package io.odinjector.binding;

public class BindingResult<T> {
	Binding<T> binding = null;
	BindingContext bindingContext = null;

	private BindingResult() {

	}

	public boolean isEmpty() {
		return binding == null;
	}

	public static <C> BindingResult<C> of(Binding<C> binding, BindingContext context) {
		BindingResult result = new BindingResult();
		result.binding = binding;
		result.bindingContext = context;
		return result;
	}

	public static <T> BindingResult<T> empty() {
		return new BindingResult<>();
	}

	public boolean isInterface() {
		return binding.isInterface();
	}

	public Binding<T> getBinding() {
		return binding;
	}

	public BindingContext getBindingContext() {
		return bindingContext;
	}
}

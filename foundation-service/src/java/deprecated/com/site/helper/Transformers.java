package com.site.helper;

import java.util.ArrayList;
import java.util.List;

public class Transformers {
	public static ListTransformer forList() {
		return ListTransformer.INSTANCE;
	}

	public static interface IBuilder<S, T> {
		public T build(S from);
	}

	public enum ListTransformer {
		INSTANCE;

		public <S, T> List<T> transform(List<S> fromList, IBuilder<S, T> builder) {
			int len = fromList.size();
			List<T> toList = new ArrayList<T>(len);

			transform(fromList, toList, builder);

			return toList;
		}

		public <S, T> void transform(List<S> fromList, List<T> toList, IBuilder<S, T> builder) {
			for (S fromItem : fromList) {
				toList.add(builder.build(fromItem));
			}
		}
	}
}

package cz.dmn.display.mynotes

import org.hamcrest.CoreMatchers
import org.junit.Assert

infix fun Any?.shouldEqual(other: Any?) {
    if (other == null) {
        Assert.assertThat(this, CoreMatchers.nullValue())
    } else {
        Assert.assertThat(this, CoreMatchers.equalTo(other))
    }
}
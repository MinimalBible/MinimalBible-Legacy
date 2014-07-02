package org.bspeice.minimalbible;

/**
 * Massive shout-out to <a href="https://github.com/vovkab">vovkab</a> for this idea.
 */
public interface Injectable {

    public Object[] getModules();

    public void inject(Object o);

}

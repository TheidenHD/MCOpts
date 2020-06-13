/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.mcopts.translation;

import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by lukas on 01.04.15.
 */
public abstract class ServerTranslations
{

    @Nonnull
    public static TranslationTextComponent join(Object... components)
    {
        return join(", ", components);
    }

    @Nonnull
    public static TranslationTextComponent join(String join, Object... components)
    {
        return new TranslationTextComponent(StringUtils.repeat("%s", join, components.length), components);
    }

    @Nonnull
    public static TranslationTextComponent join(List<?> components)
    {
        return join(components.toArray());
    }

    public <T> T object(BiFunction<String, Object[], T> fun, String key, Object... params)
    {
        if (translateServerSide())
            return fun.apply(Translations.format(key, convertParams(params)), new Object[0]);
        else
            return fun.apply(key, params);
    }

    public CommandException commandException(String key, Object... params)
    {
        return object(CommandException::new, key, params);
    }

    public CommandException wrongUsageException(String key, Object... params)
    {
        return object(WrongUsageException::new, key, params);
    }

    public ITextComponent format(String key, Object... params)
    {
        if (translateServerSide())
            return new StringTextComponent(Translations.format(key, convertParams(params)));
        else
            return new TranslationTextComponent(key, params);
    }

    public ITextComponent get(String key)
    {
        if (translateServerSide())
            return new StringTextComponent(Translations.get(key));
        else
            return new TranslationTextComponent(key);
    }

    public String usage(String key)
    {
        if (translateServerSide())
            return Translations.get(key);
        else
            return key;
    }

    public Object[] convertParams(Object... params)
    {
        if (translateServerSide())
        {
            Object[] array = new Object[params.length];

            for (int i = 0; i < array.length; i++)
            {
                if (params[i] instanceof TranslationTextComponent)
                    array[i] = Translations.format(((TranslationTextComponent) params[i]).getKey(), this.convertParams(((TranslationTextComponent) params[i]).getFormatArgs()));
                else if (params[i] instanceof ITextComponent)
                    array[i] = ((ITextComponent) params[i]).getUnformattedComponentText();
                else
                    array[i] = params[i];
            }

            return array;
        }
        else
            return params;
    }

    public abstract boolean translateServerSide();
}

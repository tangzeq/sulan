package com.netty.customer.storage;

import com.netty.customer.message.core.BaseMessage;
import com.netty.customer.message.core.TextMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：下标存储桶
 * new.index > top.index >  foot.index
 * 作者：唐泽齐
 */
@Component
public class MessageStorage {

    private static volatile int capacity = 10;
    private static volatile List<Long> indexs = new ArrayList<Long>();
    private static volatile BaseLink top = null;
    private static volatile BaseLink foot = null;

    private static BaseLink get(Long index) {
        if (!indexs.contains(index)) return null;
        BaseLink node = foot;
        if (indexs.indexOf(index) >= indexs.size() / 2) node = top;
        while (node.getIndex().compareTo(index) != 0) {
            node = node.getNext();
        }
        return node;
    }

    public static synchronized void add(BaseMessage b) {
        if (indexs.size() >= capacity) {
            remove();
        }
        b.setTime(new Date().getTime());
        b.getMessage().setTime(b.getTime());
        b.getMessage().setIndex(b.getId());
        BaseLink storage = new BaseLink();
        storage.setIndex(b.getMessage().getIndex());
        storage.setBs(b.getMessage());
        top = addTop(top, storage);
        BaseLink storage1 = new BaseLink();
        storage1.setIndex(b.getMessage().getIndex());
        storage1.setBs(b.getMessage());
        foot = addFoot(foot, storage1);
        indexs.add(storage.getIndex());
        indexs.sort(Long::compareTo);
    }

    private static BaseLink addTop(BaseLink bs, BaseLink b) {
        if (ObjectUtils.isEmpty(bs)) {
            bs = b;
            return bs;
        } else if (bs.getIndex().compareTo(b.getIndex()) < 0) {
            b.setNext(bs);
            bs = b;
            return bs;
        } else if (ObjectUtils.isEmpty(bs.getNext())) {
            bs.setNext(b);
            return bs;
        } else if (bs.getNext().getIndex().compareTo(b.getIndex()) < 0) {
            b.setNext(bs.getNext());
            bs.setNext(b);
            return bs;
        } else {
            bs.setNext(addTop(bs.getNext(), b));
            return bs;
        }
    }

    private static synchronized BaseLink addFoot(BaseLink bs, BaseLink b) {
        if (ObjectUtils.isEmpty(bs)) {
            bs = b;
            return bs;
        } else if (bs.getIndex().compareTo(b.getIndex()) > 0) {
            b.setNext(bs);
            bs = b;
            return bs;
        } else if (ObjectUtils.isEmpty(bs.getNext())) {
            bs.setNext(b);
            return bs;
        } else if (bs.getNext().getIndex().compareTo(b.getIndex()) > 0) {
            b.setNext(bs.getNext());
            bs.setNext(b);
            return bs;
        } else {
            bs.setNext(addFoot(bs.getNext(), b));
            return bs;
        }
    }

    private static synchronized void remove() {
        if (indexs.size() > 0) {
            indexs.remove(0);
            indexs.sort(Long::compareTo);
            foot = foot.getNext();
            top = removeLast(top);
        }
    }

    private static synchronized BaseLink removeLast(BaseLink bs) {
        if (ObjectUtils.isEmpty(bs.getNext())) {
            return null;
        } else {
            bs.setNext(removeLast(bs.getNext()));
            return bs;
        }
    }

    public static synchronized void clear() {
        indexs.clear();
        while (!ObjectUtils.isEmpty(top)) {
            top = top.getNext();
        }
        while (!ObjectUtils.isEmpty(foot)) {
            foot = foot.getNext();
        }
    }

    public static int size() {
        return indexs.size();
    }

    public static BaseLink read(Integer type, Long index) {
        BaseLink bs = null;
        if (indexs.size() > 0) {
            if (type.compareTo(0) == 0) bs = get(index);
            else if (type.compareTo(0) > 0) bs = readBig(index);
            else bs = readSmall(index);
        }
        if (!ObjectUtils.isEmpty(bs)) {
            BaseLink bbs = new BaseLink();
            bbs.setBs(bs.getBs());
            bbs.setIndex(bs.getIndex());
            bs = bbs;
        }
        return bs;
    }

    private static BaseLink readSmall(Long index) {
        Long middle = indexs.get(indexs.size() / 2);
        if (middle > index) {
            BaseLink node = foot;
            if (ObjectUtils.isEmpty(node.getNext())) return null;
            while (node.getNext().getIndex().compareTo(index) < 0) {
                node = node.getNext();
            }
            return node.getIndex().compareTo(index) < 0 ? node : null;
        } else {
            BaseLink node = top;
            if (ObjectUtils.isEmpty(node.getNext())) {
                return node.getIndex().compareTo(index) < 0 ? node : null;
            }
            while (node.getIndex().compareTo(index) >= 0) {
                node = node.getNext();
            }
            return node;
        }
    }

    private static BaseLink readBig(Long index) {
        Long middle = indexs.get(indexs.size() / 2);
        if (middle > index) {
            BaseLink node = foot;
            if (ObjectUtils.isEmpty(node.getNext())) {
                return node.getIndex().compareTo(index) > 0 ? node : null;
            }
            while (node.getIndex().compareTo(index) <= 0) {
                node = node.getNext();
            }
            return node;
        } else {
            BaseLink node = top;
            if (ObjectUtils.isEmpty(node.getNext())) return null;
            while (node.getNext().getIndex().compareTo(index) > 0) {
                node = node.getNext();
            }
            return node.getIndex().compareTo(index) > 0 ? node : null;
        }
    }

    public static int capacity() {
        return capacity;
    }

    public static int capacity(Integer cap) {
        if (ObjectUtils.isEmpty(cap)) return capacity;
        capacity = cap;
        while (size() > capacity) {
            remove();
        }
        return capacity;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i <= 20; i = i + 2) {
            MessageStorage.add(BaseMessage.builder().type(5).time((long) i).id((long) i).message(
                    TextMessage.builder().message(i + "").build()
            ).build());
            Thread.sleep(100);
            System.out.println("args = " + i);
        }
        Long find = -100l;
        System.out.println("1 = " + MessageStorage.read(1, find));
        System.out.println("-1 = " + MessageStorage.read(-1, find));
        System.out.println("0 = " + MessageStorage.read(0, find));
    }

}
